package dev.rocky.concurrency;

import java.util.concurrent.locks.ReentrantLock;

/** Demonstrates ordered locking to prevent deadlock during two-account transfers. */
public final class LockProtectedTransfer {
    public static final class Account {
        private final long id;
        private final ReentrantLock lock = new ReentrantLock();
        private long balance;

        public Account(long id, long balance) {
            if (balance < 0) throw new IllegalArgumentException("negative balance");
            this.id = id;
            this.balance = balance;
        }

        public long balance() {
            lock.lock();
            try { return balance; }
            finally { lock.unlock(); }
        }
    }

    public static void transfer(Account from, Account to, long amount) {
        if (from == null || to == null || from == to || amount < 0) {
            throw new IllegalArgumentException("invalid transfer");
        }
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;
        if (first.id == second.id) throw new IllegalArgumentException("account IDs must be unique");
        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (from.balance < amount) throw new IllegalArgumentException("insufficient funds");
                from.balance -= amount;
                to.balance = Math.addExact(to.balance, amount);
            } finally { second.lock.unlock(); }
        } finally { first.lock.unlock(); }
    }
}
