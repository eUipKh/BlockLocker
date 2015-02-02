package nl.rutgerkok.blocklocker.impl.updater;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;

/**
 * Checks whether an update is available.
 *
 */
final class UpdateChecker {

    private static final String UPDATE_URL = "http://rutgerkok.nl/tools/updater/blocklocker.php";

    private final JSONParser jsonParser = new JSONParser();

    /**
     * Checks online for updates. Blocking method.
     * @param currentVersion
     * @return The update result.
     * @throws IOException If an IO error occurs.
     */
    public UpdateCheckResult checkForUpdatesSync(String currentVersion) throws IOException {
        String currentVersionEncoded = URLEncoder.encode(currentVersion, "UTF-8");
        URL url = new URL(UPDATE_URL + "?version=" + currentVersionEncoded);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        InputStream stream = null;
        String response = "";
        try {
            stream = new BOMInputStream(connection.getInputStream());
            response = IOUtils.toString(stream, Charsets.UTF_8);
            Object object = jsonParser.parse(response);
            return new UpdateCheckResult((JSONObject) object);
        } catch (IOException e) {
            // Just rethrow, don't wrap
            throw e;
        } catch (ParseException e) {
            throw new IOException("Invalid JSON", e);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
}
