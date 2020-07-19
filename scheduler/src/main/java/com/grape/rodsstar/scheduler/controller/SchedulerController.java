package com.grape.rodsstar.scheduler.controller;

import com.grape.rodsstar.scheduler.csv.OrderInput;
import com.grape.rodsstar.scheduler.csv.OrderOutput;
import com.grape.rodsstar.scheduler.csv.ScheduleOutput;
import com.grape.rodsstar.scheduler.entity.OrderItem;
import com.grape.rodsstar.scheduler.entity.WorkSchedule;
import com.grape.rodsstar.scheduler.repository.OrderItemRepository;
import com.grape.rodsstar.scheduler.service.CsvService;
import com.grape.rodsstar.scheduler.service.MachineSchedulerService;
import com.grape.rodsstar.scheduler.service.OrderService;
import com.grape.rodsstar.scheduler.service.SchedulerService;
import com.grape.rodsstar.scheduler.service.ZipService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private CsvService csvService;

    @Autowired
    private MachineSchedulerService machineSchedulerService;

    @Autowired
    private ZipService zipService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PostMapping(value = "/createOptimalization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/zip")
    public byte[] createOptimalization(@RequestParam("file") MultipartFile csv, @RequestParam(value = "startDate", required = false, defaultValue = "2020.07.20. 06:00") @DateTimeFormat(pattern = "yyyy.MM.dd. HH:mm") LocalDateTime startDate) throws IOException{
        return handle(csv, startDate, false);
    }

    @PostMapping(value = "/loadSchedule", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/zip")
    public byte[] loadSchedule(@RequestParam("file") MultipartFile csv) throws IOException{
        LocalDateTime startDate = orderService.getLastOrderEnd();
        return handle(csv, startDate, true);
    }

    private byte[] handle(MultipartFile csv, LocalDateTime startDate, boolean save) throws IOException {
        List<OrderInput> input = csvService.parseOrders(csv.getInputStream());

        List<OrderItem> orders = schedulerService.createSchedule(input, startDate);
        List<OrderOutput> orderOutputs = schedulerService.createOrderOutput(orders);

        List<WorkSchedule> schedules = machineSchedulerService.createWorkSchedule(orders);
        List<ScheduleOutput> scheduleOutputs = machineSchedulerService.createWorkScheduleOutput(schedules);

        if(save){
            orderItemRepository.saveAll(orders);
        }

        Map<String, String> outputs = new HashMap<>(2);
        outputs.put("order_output.csv", csvService.createOutput(orderOutputs, OrderOutput.class));
        outputs.put("work_schedule_output.csv", csvService.createOutput(scheduleOutputs, ScheduleOutput.class));

        return zipService.zipMultipleFile(outputs);
    }
}
