/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appointments;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author kostas
 */
public class MyCalendar {

    //Hour format should be HH.mm. Minutes always "00".
    private Date[] timeArray;
    private boolean[] appointmentAvailable;
    private boolean fullDay = false;
    
    public boolean getFullDay(){
        return this.fullDay;
    }

    public Date getDate() {
        Date date = timeArray[0];
        return date;
    }

    public boolean getAppointment(int i) {
        boolean isAvailable = appointmentAvailable[i];
        return isAvailable;
    }

    //When the customer selects an available time set availability to false.
    public void setAppointment(int i) {
        appointmentAvailable[i] = false;
        //Checks if the day is still available for appoointments.
        for (int j = 0; j < 8; j++) {
            if (this.getAppointment(j)) {
                break;
            }
            fullDay = true;
        }
    }

    
    //Construts a new day containing 8 available appointments.
    public MyCalendar(Date appDate) throws ParseException {
        fullDay = false;
        timeArray = new Date[8];
        appointmentAvailable = new boolean[8];
        Date myDate = appDate;
        Calendar myCal = Calendar.getInstance();
        myCal.setTime(myDate);

        //Fills the array with available appointment times.
        try {
            for (int i = 0; i < 8; i++) {
                timeArray[i] = myDate;
                appointmentAvailable[i] = true;
                myCal.add(Calendar.HOUR, 1);
            }
        } catch (NullPointerException n) {
            n.printStackTrace();
        }
    }
}
