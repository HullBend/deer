package net.deer.cuda;

public interface Cleaner {

    // Returns 0L on success, must never throw an exception!
    long release(Address address);
}
