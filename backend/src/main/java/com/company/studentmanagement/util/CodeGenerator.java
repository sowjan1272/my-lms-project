package com.company.studentmanagement.util;

import java.time.Year;

public class CodeGenerator {

    public static String studentCode(long sequence) {
        return "STU-" + Year.now().getValue() + "-" + String.format("%04d", sequence);
    }

    public static String staffCode(long sequence) {
        return "STAFF-" + Year.now().getValue() + "-" + String.format("%04d", sequence);
    }
}
