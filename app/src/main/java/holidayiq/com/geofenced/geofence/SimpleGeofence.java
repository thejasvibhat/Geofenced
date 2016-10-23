/*
 * Copyright (C) 2014 The Android Open Source Project
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

package holidayiq.com.geofenced.geofence;

import com.google.android.gms.location.Geofence;

/**
 * A single Geofence object, defined by its center and radius.
 */
public class SimpleGeofence {

    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;
    private int mLoiteringDelay;
    private boolean isDwellRequired;

    /**
     * @param geofenceId The Geofence's request ID.
     * @param latitude Latitude of the Geofence's center in degrees.
     * @param longitude Longitude of the Geofence's center in degrees.
     * @param radius Radius of the geofence circle in meters.
     * @param transition Type of Geofence transition.
     */
    public SimpleGeofence(String geofenceId, double latitude, double longitude, float radius, int transition,int loiteringdelay,boolean isDwellRequired) {
        // Set the instance fields from the constructor.
        this.mId = geofenceId;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mRadius = radius;
        this.mTransitionType = transition;
        this.mLoiteringDelay = loiteringdelay;
        this.isDwellRequired = isDwellRequired;
    }

    // Instance field getters.
    public String getId() {
        return mId;
    }
    public double getLatitude() {
        return mLatitude;
    }
    public double getLongitude() {
        return mLongitude;
    }
    public float getRadius() {
        return mRadius;
    }
    public long getExpirationDuration() {
        return mExpirationDuration;
    }
    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a SimpleGeofence.
     * @return A Geofence object.
     */
    public Geofence toGeofence() {
        // Build a new Geofence object.
        try {
            Geofence.Builder builder = new Geofence.Builder().setRequestId(mId)
                    .setTransitionTypes(mTransitionType)
                    .setCircularRegion(mLatitude, mLongitude, mRadius)
                    .setNotificationResponsiveness(300000)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE);
            if(isDwellRequired){
                builder.setLoiteringDelay(mLoiteringDelay);
            }
            return builder.build();
        }catch (Exception e){
            return  null; // log this event
        }
    }

}
