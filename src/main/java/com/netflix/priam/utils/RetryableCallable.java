package com.netflix.priam.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

public abstract class RetryableCallable<T> implements Callable<T>
{
    public static final int DEFAULT_NUMBER_OF_RETRIES = 15;
    public static final long DEFAULT_WAIT_TIME = 100;
    private int retrys;
    private long waitTime;

    public RetryableCallable()
    {
        this.retrys = DEFAULT_NUMBER_OF_RETRIES;
        this.waitTime = DEFAULT_WAIT_TIME;
    }

    public RetryableCallable(int retrys, long waitTime)
    {
        set(retrys, waitTime);
    }

    public void set(int retrys, long waitTime)
    {
        this.retrys = retrys;
        this.waitTime = waitTime;
    }

    public abstract T retriableCall() throws Exception;

    public T call() throws Exception
    {
        int retry = 0;
        while (true)
        {
            try
            {
                return retriableCall();
            }
            catch (CancellationException e)
            {
                throw e;
            }
            catch (Exception e)
            {
                retry++;
                if (retry == retrys)
                {
                    throw e;
                }
                Thread.sleep(waitTime);
            }
            finally
            {
                forEachExecution();
            }
        }
    }

    public void forEachExecution()
    {
        // do nothing by default.
    }
}