# AppLocation
**Welcome to the AppLocation !**

AppLocation is used to get user current location and you can get user current address as well. By Using this library you just need to extends the class or you can implement the interface to get user latitude & longitude.

**To Use this library just follow the simple steps given below -**

## Step : 1 - 
Add `jitpack.io` in the Project Level _build.gradle_ file
```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```
Add `dependencies` in the Module Level _build.gradle_ file 
( **Use Current Version** - [![](https://jitpack.io/v/WackyCodes/AppLocation.svg)](https://jitpack.io/#WackyCodes/AppLocation) )
```groovy
dependencies {
       implementation 'com.github.WackyCodes:AppLocation:1.0.1'
}
```

## Step : 2 - Now You have Multiple Options to get the current location

### Option 1 - Use Activity

extends `ActivityGetLocationLocation` and override `onReceiveLatLng()` method.
You can use below methods after for the rest of the process.
```java
        String getAddressLine( );
        double getLatitude();
        double getLongitude();
```

### Option 2 - Use DialogFragment

extends `FragmentDialogGetLocationLocation` and override `onReceiveLatLng()` method.
You can use below methods after for the rest of the process.
```java
        String getAddressLine( );
        double getLatitude();
        double getLongitude();
```

### Option 3 - Use Custom Code ( GPSTracker )

To implement this option, you need to follow some steps -

**Step 3.1:-** implements `GPSTracker.OnGpsListener`

**Step 3.2:-** Use below code -
```java
    // GPSTracker is main helper class to do most of the things
    private GPSTracker gpsTracker;
    private boolean isGPSOn = false;

    /** Activity Launcher to Enable Location!
     * You can use it or get activity Result by using onActivityResult() */
    private ActivityResultLauncher<IntentSenderRequest> registerForActivityResult =
            registerForActivityResult( new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                // Call gpsStatus until We didn't get GPS Enable
                if (result.getResultCode() == RESULT_OK) {
                    gpsStatus(true );
                }else{
                    gpsStatus(false );
                }
            });

```

**Step 3.3-** Assign the `gpsTracker` variable in the `onStart` method and use below code
```java
 // Assign the gpsTracker ..!
        gpsTracker = new GPSTracker( MainActivity.this );
        if ( isLocationPermissionGranted() ) {
            /** Step 1 :- Use Either this
             * If You use Else part in the @gpsStatus method then
             * You can Skip the below code!
             * App System Dialog to enable GPS
             */
            gpsTracker.initGPSEnabled();
            gpsTracker.turnGPSOn(this, registerForActivityResult );

            /** Step 1 :- Or This !
             * If You use Else part in the @gpsStatus method then
             * You can directly call only this method!
             */
//            gpsStatus( isGPSOn );

        }else{
//            requestForPermission( PERMISSION_CODE_LOCATION );
        }
```

**Step 3.4-** Final method `getCurrentLocation`, you can check below ( Optional ) 
```java
   // Get User Location : Device Location !
    public void getCurrentLocation() {
        runOnUiThread(() -> {
            gpsTracker.queryToLoadLocation();

            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            String addressLine = gpsTracker.getAddressLine(this);

            if ( addressLine == null || ( latitude + "," + longitude ).equalsIgnoreCase("0.0,0.0")) {
                /** Call setOnLoadFusedLocation if User Location Not found
                 * If you wants to used Fused location, call it!
                 */
                gpsTracker.setOnLoadFusedLocation( this, this );
            }else {
                onLoadGPSLocation( latitude, longitude, addressLine );
            }
        });
    }
```


***

That's It. Happy Coding :)
Now enjoy  follow me on [Instagram ](https://www.instagram.com/wackycodes_/)to know more tips.
