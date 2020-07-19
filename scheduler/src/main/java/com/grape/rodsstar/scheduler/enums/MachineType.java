package com.grape.rodsstar.scheduler.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MachineType {

    CUTTER("Vágó"),
    BENDER("Hajlító"),
    WELDER("Hegesztő"),
    TESTER("Tesztelő"),
    PAINTER("Festő"),
    WRAPPER("Csomagoló");

    private String name;
}
