#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistentCookieStore implements CookieStore, Runnable {

    CookieStore store;
    Gson gson;
    final String outputFile = "cookies.json";

    private HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;

    public PersistentCookieStore() {

        store = new CookieManager().getCookieStore();
        gson = new Gson();

        try {
            Type typeOfHashMap = new TypeToken<HashMap<String, ConcurrentHashMap<String, HttpCookie>>>() {
            }.getType();
            Reader reader = new FileReader(outputFile);

            cookies = gson.fromJson(reader, typeOfHashMap);
        } catch (FileNotFoundException | JsonSyntaxException | JsonIOException | NullPointerException e) {
            System.err.println("Oops. File doesn't exist");
            cookies = new HashMap<>();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        //System.out.println("Adding: "+gson.toJson(cookie));
        String name = getCookieToken(uri, cookie);
        // Save cookie into local store, or remove if expired
        if (!cookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost())) {
                cookies.put(uri.getHost(), new ConcurrentHashMap<>());
            }
            cookies.get(uri.getHost()).put(name, cookie);
        } else if (cookies.containsKey(uri.toString())) {
            cookies.get(uri.getHost()).remove(name);
        }
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> ret = new ArrayList<>();
        if (cookies.containsKey(uri.getHost())) {
            ret.addAll(cookies.get(uri.getHost()).values());
        }
        return ret;
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            ret.addAll(cookies.get(key).values());
        }

        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<>();
        for (String key : cookies.keySet()) {
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);

        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeAll() {
        cookies.clear();
        return true;
    }

    @Override
    public void run() {
        try {
            Writer writer = new FileWriter(outputFile);
            gson.toJson(cookies, writer);
            writer.flush();
            System.out.println("Cookies saved");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    protected String getCookieToken(URI uri, HttpCookie cookie) {
        return cookie.getName() + cookie.getDomain();
    }

}
