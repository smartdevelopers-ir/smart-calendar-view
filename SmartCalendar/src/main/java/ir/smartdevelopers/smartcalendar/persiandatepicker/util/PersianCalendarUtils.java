/**
 * Persian Calendar see: http://code.google.com/p/persian-calendar/
 * Copyright (C) 2012  Mortezaadi@gmail.com
 * PersianCalendarUtils.java
 * <p>
 * Persian Calendar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU arr_info_students Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU arr_info_students Public License for more details.
 * <p>
 * You should have received a copy of the GNU arr_info_students Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ir.smartdevelopers.smartcalendar.persiandatepicker.util;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * algorithms for converting Julian days to the Persian calendar, and vice versa
 * are adopted from <a
 * href="casema.nl/couprie/calmath/persian/index.html">couprie.nl</a> written in
 * VB. The algorithms is not exactly the same as its original. I've done some
 * minor changes in the sake of performances and corrected some bugs.
 *
 * @author Morteza contact: <a
 *         href="mailto:Mortezaadi@gmail.com">Mortezaadi@gmail.com</a>
 * @version 1.0
 *
 */
public class PersianCalendarUtils
{

    /**
     * Converts a provided Persian (Shamsi) date to the Julian Day Number (i.e.
     * the number of days since January 1 in the year 4713 BC). Since the
     * Persian calendar is a highly regular calendar, converting to and from a
     * Julian Day Number is not as difficult as it looks. Basically it's a
     * mather of dividing, rounding and multiplying. This routine uses Julian
     * Day Number 1948321 as focal point, since that Julian Day Number
     * corresponds with 1 Farvardin (1) 1.
     *
     * @param year
     *            int persian year
     * @param month
     *            int persian month
     * @param day
     *            int persian day
     * @return long
     */
    public static long persianToJulian(long year, int month, int day)
    {
        return 365L * ((ceil(year - 474L, 2820D) + 474L) - 1L) + ((long) Math.floor((682L * (ceil(year - 474L, 2820D) + 474L) - 110L) / 2816D)) + (PersianCalendarConstants.PERSIAN_EPOCH - 1L) + 1029983L
                * ((long) Math.floor((year - 474L) / 2820D)) + (month < 7 ? 31 * month : 30 * month + 6) + day;
    }

    /**
     * Calculate whether current year is Leap year in persian or not
     *
     * @return boolean
     */
    public static boolean isPersianLeapYear(int persianYear)
    {

        int mod = persianYear % 33;
        for( int r : PersianCalendarConstants.LEAP_RATES){
            if(r == mod){
                return true;
            }
        }
        return  false;
//        return PersianCalendarUtils.ceil((38D + (PersianCalendarUtils.ceil(persianYear - 474L, 2820L) + 474L)) * 682D, 2816D) < 682L;
    }
    public static long persianToMillis(int persianYear, int persianMonth, int persianDay) {
        // تبدیل شمسی به میلادی
        int[] gregorianDate = persianToGregorian(persianYear, persianMonth, persianDay);
        int gregorianYear = gregorianDate[0];
        int gregorianMonth = gregorianDate[1]; // ماه از 1 تا 12
        int gregorianDay = gregorianDate[2];

        // تنظیم تاریخ میلادی در Calendar و گرفتن میلی‌ثانیه
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
        calendar.set(gregorianYear, gregorianMonth , gregorianDay, 0, 0, 0); // ماه از 0 تا 11
        calendar.set(Calendar.MILLISECOND, 0); // صفر کردن میلی‌ثانیه
        return calendar.getTimeInMillis();
    }

    public static int[] persianToGregorian(int persianYear, int persianMonth, int persianDay) {
        // محاسبات تبدیل شمسی به میلادی
        int jy = persianYear - 979;
        int jm = persianMonth ;
        int jd = persianDay - 1;

        int jDayNo = 365 * jy + (jy / 33) * 8 + (jy % 33 + 3) / 4;
        for (int i = 0; i < jm; ++i) {
            jDayNo += new int[]{31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30}[i];
        }
        jDayNo += jd;

        int gDayNo = jDayNo + 79;
        int gy = 1600 + 400 * (gDayNo / 146097);
        gDayNo = gDayNo % 146097;

        int leap = 1;
        if (gDayNo >= 36525) {
            gDayNo--;
            gy += 100 * (gDayNo / 36524);
            gDayNo = gDayNo % 36524;
            if (gDayNo >= 365) {
                gDayNo++;
            } else {
                leap = 0;
            }
        }

        gy += 4 * (gDayNo / 1461);
        gDayNo %= 1461;

        if (gDayNo >= 366) {
            leap = 0;
            gDayNo--;
            gy += gDayNo / 365;
            gDayNo = gDayNo % 365;
        }

        int gm, gd;
        int i;
        for (i = 0; gDayNo >= new int[]{31, 28 + leap, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}[i]; ++i) {
            gDayNo -= new int[]{31, 28 + leap, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}[i];
        }
        gm = i ;
        gd = gDayNo + 1;

        return new int[]{gy, gm, gd}; // سال، ماه، روز میلادی
    }
    /**
     * Converts a provided Julian Day Number (i.e. the number of days since
     * January 1 in the year 4713 BC) to the Persian (Shamsi) date. Since the
     * Persian calendar is a highly regular calendar, converting to and from a
     * Julian Day Number is not as difficult as it looks. Basically it's a
     * mather of dividing, rounding and multiplying.
     *
     * @param julianDate
     * @return long
     */
    public static long julianToPersian(long julianDate)
    {
        long persianEpochInJulian = julianDate - persianToJulian(475L, 0, 1);
        long cyear = ceil(persianEpochInJulian, 1029983D);
        long ycycle = cyear != 1029982L ? ((long) Math.floor((2816D * (double) cyear + 1031337D) / 1028522D)) : 2820L;
        long year = 474L + 2820L * ((long) Math.floor(persianEpochInJulian / 1029983D)) + ycycle;
        long aux = (1L + julianDate) - persianToJulian(year, 0, 1);
        int month = (int) (aux > 186L ? Math.ceil((double) (aux - 6L) / 30D) - 1 : Math.ceil((double) aux / 31D) - 1);
        int day = (int) (julianDate - (persianToJulian(year, month, 1) )) + 1;
        return (year << 16) | (month << 8) | day;
    }
    public static int[] millisToJalali(long timeInMillis) {
        // تبدیل میلی‌ثانیه به تاریخ میلادی
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        int gYear = calendar.get(Calendar.YEAR);
        int gMonth = calendar.get(Calendar.MONTH) ; // ماه از 0 شروع می‌شه
        int gDay = calendar.get(Calendar.DAY_OF_MONTH);

        // تبدیل تاریخ میلادی به شمسی
        return gregorianToJalali(gYear, gMonth, gDay);
    }
    /**
     * @param gMonth starts from 0
     * */
    public static int[] gregorianToJalali(int gYear, int gMonth, int gDay) {
        int[] jalaliDate = new int[3];
        int gy = gYear - 1600;
        int gm = gMonth ;
        int gd = gDay - 1;

        int gDayNo = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;
        for (int i = 0; i < gm; ++i) {
            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            gDayNo += daysInMonth[i];
        }
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0))) {
            gDayNo += 1; // سال کبیسه
        }
        gDayNo += gd;

        int jDayNo = gDayNo - 79;
        int jNp = jDayNo / 12053;
        jDayNo %= 12053;

        int jy = 979 + 33 * jNp + 4 * (jDayNo / 1461);
        jDayNo %= 1461;

        if (jDayNo >= 366) {
            jy += (jDayNo - 1) / 365;
            jDayNo = (jDayNo - 1) % 365;
        }

        int jm, jd;
        int i;
        for (i = 0; i < 11 && jDayNo >= new int[]{31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30}[i]; ++i) {
            jDayNo -= new int[]{31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30}[i];
        }
        jm = i ;
        jd = jDayNo + 1;

        jalaliDate[0] = jy; // سال شمسی
        jalaliDate[1] = jm; // ماه شمسی
        jalaliDate[2] = jd; // روز شمسی
        return jalaliDate;
    }
    /**
     * Ceil function in original algorithm
     *
     * @param double1
     * @param double2
     * @return long
     */
    public static long ceil(double double1, double double2)
    {
        return (long) (double1 - double2 * Math.floor(double1 / double2));
    }

}
