package net.deer.cuda;

public interface Cleaner {

    // must return 0 on success (<> 0 otherwise) and must never throw an exception
    long release(Address address);
}
