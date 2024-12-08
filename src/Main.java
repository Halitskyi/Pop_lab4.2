import java.util.concurrent.*;

public class Main {
    static final int NUM_PHILOSOPHERS = 5;
    static final Semaphore[] forks = new Semaphore[NUM_PHILOSOPHERS];
    static final Semaphore mutex = new Semaphore(1);
    static final Semaphore maxForks = new Semaphore(NUM_PHILOSOPHERS - 1); // Обмеження: не більше 4 філософів можуть їсти одночасно

    static class Philosopher extends Thread {
        private final int id;

        public Philosopher(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    maxForks.acquire(); // Перевірка, чи можуть більше чотирьох філософів взяти виделки

                    mutex.acquire(); // Блокування доступу до критичної ділянки
                    pickUpForks();
                    mutex.release();

                    eat();
                    putDownForks();
                    maxForks.release(); // Випуск ресурсу, щоб інші могли поїсти
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void pickUpForks() throws InterruptedException {
            System.out.println("Philosopher " + id + " is picking up forks.");
            forks[id].acquire();
            forks[(id + 1) % NUM_PHILOSOPHERS].acquire();
        }

        private void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating.");
            Thread.sleep((long) (Math.random() * 1000));
        }

        private void putDownForks() {
            System.out.println("Philosopher " + id + " is putting down forks.");
            forks[id].release();
            forks[(id + 1) % NUM_PHILOSOPHERS].release();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            forks[i] = new Semaphore(1);
        }

        Philosopher[] philosophers = new Philosopher[NUM_PHILOSOPHERS];
        for (int i = 0; i < NUM_PHILOSOPHERS; i++) {
            philosophers[i] = new Philosopher(i);
            philosophers[i].start();
        }
    }
}
