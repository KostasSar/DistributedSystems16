/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author kostas
 */
public class Appointments {

    /**
     * @param args the command line arguments
     * @throws java.text.ParseException
     */
    public static void main(String[] args) throws ParseException {

        int dayOfWeek;
        Date d1;
        boolean wrongDay = false;

        //CHECK IF DATE EXISTS ALREADY. THEN CREATE A NEW ONE.
        ArrayList<MyCalendar> DateSchedule = new ArrayList<>();

        do {
            if (wrongDay) {
                System.out.println("Not a workday. Pick a day between Monday and Friday.");
                //REDIRECT TO APPOINTMENT FORM
            }
            wrongDay = true;
            //EXAMPLE DATE INPUT USED FOR TESTING. FIRST PART IS GOING TO BE REPLACED BY FORM INPUT.
            String formDateInput = "2015/02/03";
            String dateInput = formDateInput + ", 09:00";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd, HH:mm");
            Date d2 = sdf.parse(dateInput);
            long d2Mili = d2.getTime();

            d1 = new Date(d2Mili);

            //CHECK EXISTING DAYS
            for (MyCalendar myOtherCalendar : DateSchedule) {
                if ((d1.compareTo(myOtherCalendar.getDate()) == 0)) {
                    //gets available appointments
                    System.out.println("Available apointments on that day");
                    for (int i = 0; i < 8; i++) {
                        if (myOtherCalendar.getAppointment(i)) {
                            System.out.println("At " + (9 + i) + ".00");
                        }
                    }
                }

            }

            //IF DOES NOT EXIST
            Calendar c1 = Calendar.getInstance();
            c1.setTime(d1);
            System.out.println("today is " + d1.toString());//OUTPUT today is Sun Feb 01 09:00:00 EET 2015
            dayOfWeek = c1.get(Calendar.DAY_OF_WEEK);

        } while ((Calendar.SATURDAY == dayOfWeek) || (Calendar.SUNDAY == dayOfWeek));
        // present date in calendar and available hours in checkbox

        MyCalendar myCalendar = new MyCalendar(d1);
        System.out.println("Available apointments on that day");
                    for (int i = 0; i < 8; i++) {
                        if (myCalendar.getAppointment(i)) {
                            System.out.println("At " + (9 + i) + ".00");
                        }
                    }

        int appDays = DateSchedule.size();
        //DateSchedule list is sorted by date.

        Date beforeDate = null;
        Date afterDate = null;
        Date currDate = null;

        for (int pos = 0; pos < appDays; pos++) {

            try {
                beforeDate = DateSchedule.get(pos - 1).getDate();
            } catch (NullPointerException n) {
                beforeDate = null;
            }
            try {
                afterDate = DateSchedule.get(pos).getDate();
            } catch (NullPointerException n) {
                afterDate = null;
            }
            currDate = myCalendar.getDate();

            if ((beforeDate == null) && (afterDate == null)) {
                DateSchedule.add(pos, myCalendar);

            } else if ((beforeDate == null) && (currDate.before(afterDate))) {
                DateSchedule.add(pos, myCalendar);

            } else if ((currDate.after(beforeDate)) && (currDate.before(afterDate))) {
                DateSchedule.add(pos, myCalendar);

            } else if ((currDate.after(beforeDate)) && (afterDate == null)) {
                DateSchedule.add(pos, myCalendar);
            }
        }
        //IF IT DOES EXIST
    } //Deletes all days, including appointments, before the current day

    public void deletePastDays(ArrayList<MyCalendar> DateSchedule) {
        Date today = new Date();
        for (MyCalendar myOtherCalendar : DateSchedule) {
            if (today.after(myOtherCalendar.getDate())) {
                //REMOVE DAY 
            }
        }
    }

}
