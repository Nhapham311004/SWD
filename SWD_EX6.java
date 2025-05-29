import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

// Strategy Interface
interface ContentComparisonStrategy {
    boolean compare(String url, String previousContent, String currentContent) throws IOException;
}

// Concrete Strategies
class SizeComparisonStrategy implements ContentComparisonStrategy {
    @Override
    public boolean compare(String url, String previousContent, String currentContent) {
        return previousContent.length() == currentContent.length();
    }
}

class ExactHtmlComparisonStrategy implements ContentComparisonStrategy {
    @Override
    public boolean compare(String url, String previousContent, String currentContent) {
        return previousContent.equals(currentContent);
    }
}

class TextContentComparisonStrategy implements ContentComparisonStrategy {
    @Override
    public boolean compare(String url, String previousContent, String currentContent) {
        // Simple implementation - in real world you might use HTML parsers
        String previousText = previousContent.replaceAll("<[^>]*>", "").trim();
        String currentText = currentContent.replaceAll("<[^>]*>", "").trim();
        return previousText.equals(currentText);
    }
}

// Observer Interface
interface Observer_EX6 {
    void update(String url, String username, String message);
}

interface Observable_EX6 {
    void registerObserver(Observer_EX6 observer);
    void removeObserver(Observer_EX6 observer);
    void notifyObservers(String url, String username, String message);
}

class AuthenticationService_EX6 {
    public User_EX6 register(String username, String email, String password) {
        String userId = "user-" + UUID.randomUUID();
        return new User_EX6(userId, username, email, password);
    }
}

class NotificationPreferences_EX6 {
    private String frequency;
    private String channel;
    private String comparisonStrategy;

    public NotificationPreferences_EX6(String frequency, String channel, String comparisonStrategy) {
        this.frequency = frequency;
        this.channel = channel;
        this.comparisonStrategy = comparisonStrategy;
    }

    public String getFrequency() { return frequency; }
    public String getChannel() { return channel; }
    public String getComparisonStrategy() { return comparisonStrategy; }
}

class Subscription_EX6 {
    private String subscriptionId;
    private String url;
    private NotificationPreferences_EX6 preferences;

    public Subscription_EX6(String subscriptionId, String url, NotificationPreferences_EX6 preferences) {
        this.subscriptionId = subscriptionId;
        this.url = url;
        this.preferences = preferences;
    }

    public String getSubscriptionId() { return subscriptionId; }
    public String getUrl() { return url; }
    public NotificationPreferences_EX6 getPreferences() { return preferences; }
}

class User_EX6 {
    private String userId;
    private String username;
    private String email;
    private String password;
    private List<Subscription_EX6> subscriptions;

    public User_EX6(String userId, String username, String email, String password) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.subscriptions = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<Subscription_EX6> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(String url, NotificationPreferences_EX6 preferences) {
        String subscriptionId = "sub-" + UUID.randomUUID();
        subscriptions.add(new Subscription_EX6(subscriptionId, url, preferences));
    }
}

class WebsiteMonitor_EX6 implements Observable_EX6 {
    private Map<String, String> urlContents = new HashMap<>();
    private List<Observer_EX6> observers = new ArrayList<>();
    private Map<String, ContentComparisonStrategy> strategies;

    public WebsiteMonitor_EX6() {
        strategies = new HashMap<>();
        strategies.put("size", new SizeComparisonStrategy());
        strategies.put("exact", new ExactHtmlComparisonStrategy());
        strategies.put("text", new TextContentComparisonStrategy());
    }

    @Override
    public void registerObserver(Observer_EX6 observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer_EX6 observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String url, String username, String message) {
        for (Observer_EX6 observer : observers) {
            observer.update(url, username, message);
        }
    }

    public void checkForUpdates(Subscription_EX6 subscription, String username) {
        String url = subscription.getUrl();
        String strategyType = subscription.getPreferences().getComparisonStrategy();
        ContentComparisonStrategy strategy = strategies.get(strategyType);

        if (strategy == null) {
            System.err.println("Unknown comparison strategy: " + strategyType);
            return;
        }

        try {
            String currentContent = fetchWebPageContent(url);

            if (!urlContents.containsKey(url)) {
                urlContents.put(url, currentContent);
                return;
            }

            String previousContent = urlContents.get(url);
            boolean isSame = strategy.compare(url, previousContent, currentContent);

            if (!isSame) {
                urlContents.put(url, currentContent);
                notifyObservers(url, username, "Change detected at " + url + " using " + strategyType + " comparison");
            }
        } catch (IOException e) {
            System.err.println("Error checking URL: " + e.getMessage());
        }
    }

    private String fetchWebPageContent(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        try (Scanner scanner = new Scanner(conn.getInputStream())) {
            scanner.useDelimiter("\\Z");
            return scanner.next();
        }
    }
}

class NotificationService_EX6 implements Observer_EX6 {
    @Override
    public void update(String url, String username, String message) {
        System.out.println("Notification to " + username + ": " + message);
    }
}

public class SWD_EX6 {
    public static void main(String[] args) throws InterruptedException {
        AuthenticationService_EX6 authService = new AuthenticationService_EX6();
        WebsiteMonitor_EX6 monitor = new WebsiteMonitor_EX6();
        NotificationService_EX6 notificationService = new NotificationService_EX6();

        monitor.registerObserver(notificationService);

        User_EX6 user = authService.register("Nhapham", "phamthanhnha311004@gmail.com", "12345");

        // Create subscriptions with different comparison strategies
        NotificationPreferences_EX6 sizePrefs = new NotificationPreferences_EX6("daily", "email", "size");
        NotificationPreferences_EX6 exactPrefs = new NotificationPreferences_EX6("daily", "email", "exact");
        NotificationPreferences_EX6 textPrefs = new NotificationPreferences_EX6("daily", "email", "text");

        user.addSubscription("https://www.weather.com", sizePrefs);
        user.addSubscription("https://www.news.com", exactPrefs);
        user.addSubscription("https://www.blog.com", textPrefs);

        for (int i = 0; i < 10; i++) {
            System.out.println("\nCheck #" + (i+1));
            for (Subscription_EX6 sub : user.getSubscriptions()) {
                monitor.checkForUpdates(sub, user.getUsername());
            }
            Thread.sleep(5000);
        }
    }
}