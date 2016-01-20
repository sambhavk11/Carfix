package my.carfix.carfix.fragment;

public class CarfixAPIHelper
{
    private static boolean production = false;

    private static String PRODUCTION_URL = "http://www.carfix.my";

    private static String DEVELOPMENT_URL = "http://112.137.171.29/Carfix";

    private static String WEB_SERVICE_URL = "http://54.179.190.147:8080/CarfixWebService";

    private static String SUBS_POLICY_NOTIFICATION_API = "/MotorAssist/SubsPolicyNotification?VehReg=%s";

    private static String REGISTER_VEHICLE_DEVICE_ID_API = "/RegisterVehicleDeviceID?VehRegNo=%s&DeviceID=%s";

    public static String generateSubsPolicyNotificationAPIURL(String vehRegNo)
    {
        String apiString = String.format(SUBS_POLICY_NOTIFICATION_API, vehRegNo);

        return (production ? PRODUCTION_URL : DEVELOPMENT_URL) + apiString;
    }

    public static String generateRegisterVehicleDeviceIDAPIURL(String vehRegNo, String deviceID)
    {
        String apiString = String.format(REGISTER_VEHICLE_DEVICE_ID_API, vehRegNo, deviceID);

        return WEB_SERVICE_URL + apiString;
    }
}
