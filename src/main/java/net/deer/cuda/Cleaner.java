package net.deer.cuda;

public interface Cleaner {

    long release(int deviceId, Address address) throws CudaException;
}
