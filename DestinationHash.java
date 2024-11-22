import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class DestinationHash {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar test.jar <roll_number> <path_to_json_file>");
            return;
        }

        String rollNumber = args[0].toLowerCase().trim();
        String jsonFilePath = args[1];

        System.out.println("Roll Number: " + rollNumber);
        System.out.println("JSON File Path: " + jsonFilePath);

        try {
            // Read the JSON file
            String content = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
            JSONObject jsonObject = new JSONObject(content);

            // Find the "destination" value
            String destinationValue = findDestination(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random string
            String randomString = generateRandomString(8);

            // Concatenate values and generate MD5 hash
            String concatenatedValue = rollNumber + destinationValue + randomString;
            String hash = generateMD5Hash(concatenatedValue);

            // Print the result
            System.out.println(hash + ";" + randomString);
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    // Function to traverse the JSON and find the first instance of "destination"
    private static String findDestination(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String found = findDestination((JSONObject) value);
                if (found != null) return found;
            }
        }
        return null;
    }

    // Function to generate an 8-character alphanumeric random string
    private static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // Function to generate MD5 hash
    private static String generateMD5Hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();

        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
