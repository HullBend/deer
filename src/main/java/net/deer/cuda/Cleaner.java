package net.deer.cuda;

public interface Cleaner {

    // Return 0L on success, never throw an exception
    long release(Address address);
}
