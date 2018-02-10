package net.deer.cuda;

public interface Cleaner {

    long release(Address address) throws CudaException;
}
