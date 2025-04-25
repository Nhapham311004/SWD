public class CollatzVerifier {
    public static boolean reachesOne(long n) {
        while (n != 1) {
            if (n % 2 == 0)
                n /= 2;
            else
                n = 3 * n + 1;
        }
        return true; 
    }

    public static void main(String[] args) {
        final int LIMIT = 1_000_000;

        for (int i = 1; i < LIMIT; i++) {
            if (!reachesOne(i)) {
                System.out.println("Failed at: " + i);
                return;
            }
        }

        System.out.println("Verified");
    }
}
