package com.infotechnano.nanocommerce.utils;

import java.util.Calendar;

public class DateHelper {

    public DateHelper() {

    }

    public Integer formatMonth(String currentMonth){
        switch (currentMonth.toLowerCase()){
            case "jan":
                return 1;
            case "feb":
                return 2;
            case "mar":
                return 3;
            case "apr":
                return 4;
            case "may":
                return 5;
            case "jun":
                return 6;
            case "jul":
                return 7;
            case "aug":
                return 8;
            case "sep":
                return 9;
            case "oct":
                return 10;
            case "nov":
                return 11;
            case "dec":
                return 12;
            default:
                return 1;
        }
    }

    public Integer formatDay(String currentDay){
        if(currentDay.charAt(0) == '0'){
            return Integer.parseInt(currentDay.substring(1));
        }
        return Integer.parseInt(currentDay);
    }

    public Integer switchToCalendar(Integer month){
        switch (month){
            case 1:
                return Calendar.JANUARY;
            case 2:
                return Calendar.FEBRUARY;
            case 3:
                return Calendar.MARCH;
            case 4:
                return Calendar.APRIL;
            case 5:
                return Calendar.MAY;
            case 6:
                return Calendar.JUNE;
            case 7:
                return Calendar.JULY;
            case 8:
                return Calendar.AUGUST;
            case 9:
                return Calendar.SEPTEMBER;
            case 10:
                return Calendar.OCTOBER;
            case 11:
                return Calendar.NOVEMBER;
            case 12:
                return Calendar.DECEMBER;
            default:
                return Calendar.JANUARY;
        }
    }

    public Integer twoDaysBackPrevMonth(Integer month,Integer day){
        if(day == 1){
            switch(month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 30;
                case 2:
                    return 27;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 29;
                default:
                    return 30;
            }
        }else{
            switch(month){
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    return 31;
                case 2:
                    return 28;
                case 4:
                case 6:
                case 9:
                case 11:
                    return 30;
                default:
                    return 31;
            }
        }
    }
}
