/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.shakyou;

import com.shakyou.TimeZone;

/**
 * Gregorian calendar implementation.
 *
 * @author Masayoshi Okutsu
 * @since 1.5
 */

public class Gregorian extends BaseCalendar {

    static class DateShakyou extends BaseCalendar.Date {
        protected DateShakyou() {
            super();
        }

        protected DateShakyou(TimeZone zone) {
            super(zone);
        }

        public int getNormalizedYear() {
            return getYear();
        }

        public void setNormalizedYear(int normalizedYear) {
            setYear(normalizedYear);
        }
    }

    Gregorian() {
    }

    public String getName() {
        return "gregorian";
    }

    public DateShakyou getCalendarDate() {
        return getCalendarDate(System.currentTimeMillis(), newCalendarDate());
    }

    public DateShakyou getCalendarDate(long millis) {
        return getCalendarDate(millis, newCalendarDate());
    }

    public DateShakyou getCalendarDate(long millis, CalendarDate date) {
        return (DateShakyou) super.getCalendarDate(millis, date);
    }

    public DateShakyou getCalendarDate(long millis, TimeZone zone) {
        return getCalendarDate(millis, newCalendarDate(zone));
    }

    public DateShakyou newCalendarDate() {
        return new DateShakyou();
    }

    public DateShakyou newCalendarDate(TimeZone zone) {
        return new DateShakyou(zone);
    }
}
