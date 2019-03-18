/* 
 * Copyright (C) Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.manolodominguez.opensimmpls.hardware.timer;

/**
 * This class implements a timestamp.
 *
 * @author Manuel Domínguez Dorado - ingeniero@ManoloDominguez.com
 * @version 2.0
 */
public class TTimestamp implements Comparable {

    /**
     * This method is the constructor of the class. Is creates a new instance of
     * TTimestamp that represent a given moment. Through this timestamp, a
     * moment is represented as M:N where M=milliseconds and N=nanoseconds.
     *
     * @param millisecond The millisecond part of the timestamp.
     * @param nanosecond The nanosecond part of the timestamp.
     * @since 2.0
     */
    public TTimestamp(long millisecond, int nanosecond) {
        this.millisecond = millisecond;
        this.nanosecond = nanosecond;
    }

    /**
     * This method compares the current timestamp with another that is specified
     * as an argument.
     *
     * @param anotherTimestamp An external TTimestamp instace to be compared
     * with the current one.
     * @return ARGUMENT_IS_GREATER, BOTH_ARE_EQUAL or ARGUMENT_IS_LOWER,
     * depending on whether the specified argument is greater, equal or lower
     * than the current instance.
     * @since 2.0
     */
    @Override
    public int compareTo(Object anotherTimestamp) {
        TTimestamp argument = (TTimestamp) anotherTimestamp;
        if (this.millisecond < argument.getMillisecond()) {
            return TTimestamp.THIS_LOWER;
        }
        if (this.millisecond > argument.getMillisecond()) {
            return TTimestamp.THIS_GREATER;
        }
        if (this.millisecond == argument.getMillisecond()) {
            if (this.nanosecond < argument.getNanosecond()) {
                return TTimestamp.THIS_LOWER;
            }
            if (this.nanosecond > argument.getNanosecond()) {
                return TTimestamp.THIS_GREATER;
            }
            if (this.nanosecond == argument.getNanosecond()) {
                return TTimestamp.THIS_EQUAL;
            }
        }
        return TTimestamp.THIS_EQUAL;
    }

    /**
     * This method gets the nanosecond component of the timestamp.
     *
     * @return The nanosecond component of the timestamp.
     * @since 2.0
     */
    public int getNanosecond() {
        return this.nanosecond;
    }

    /**
     * This method set the nanosecond component of the timestamp.
     *
     * @param nanosecond The nanosecond component of the timestamp.
     * @since 2.0
     */
    public void setNanosecond(int nanosecond) {
        this.nanosecond = nanosecond;
    }

    /**
     * This method adds the specified number of nanoseconds to the current
     * timestamp.
     *
     * @param addedNanosecond The number of nanoseconds to be added to the
     * current timestamp.
     * @since 2.0
     */
    public void increaseNanoseconds(int addedNanosecond) {
        //FIX: Avoid using harcoded values. Use class constant instead.
        this.nanosecond += addedNanosecond;
        long integerDivision = (this.nanosecond / 1000000);
        if (integerDivision > 0) {
            increaseMiliseconds(integerDivision);
            this.nanosecond %= 1000000;
        }
    }

    /**
     * This method get the millisecond component of the timestamp.
     *
     * @return The millisecond component of the timestamp.
     * @since 2.0
     */
    public long getMillisecond() {
        return this.millisecond;
    }

    /**
     * This method set the millisecond component of the timestamp.
     *
     * @param millisecond The millisecond component of the timestamp.
     * @since 2.0
     */
    public void setMillisecond(long millisecond) {
        this.millisecond = millisecond;
    }

    /**
     * This method set the current timestamp using the values of another
     * timestamp specified as an argument.
     *
     * @param anotherTimestamp A timestamp that will be used to set the values
     * of the current one..
     * @since 2.0
     */
    public void setTimestamp(TTimestamp anotherTimestamp) {
        this.millisecond = anotherTimestamp.getMillisecond();
        this.nanosecond = anotherTimestamp.getNanosecond();
    }

    /**
     * This method adds the specified number of milliseconds to the current
     * timestamp.
     *
     * @param addedMilliseconds The number of milliseconds to be added to the
     * current timestamp.
     * @since 2.0
     */
    public void increaseMiliseconds(long addedMilliseconds) {
        this.millisecond += addedMilliseconds;
    }

    /**
     * This method gets the moment represented by the current timestamp, in
     * nanoseconds.
     *
     * @return The moment represented by the current timestamp, in nanoseconds.
     * @since 2.0
     */
    public long getTotalAsNanoseconds() {
        //FIX: Avoid using harcoded values. Use class constant instead.
        return (long) ((this.getMillisecond() * 1000000) + this.getNanosecond());
    }
    public static final int THIS_LOWER = -1;
    public static final int THIS_EQUAL = 0;
    public static final int THIS_GREATER = 1;
    private long millisecond;
    private int nanosecond;
}
