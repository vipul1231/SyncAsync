package org.test.syncasync.flow;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import lombok.Data;

import java.util.concurrent.Future;
import java.util.function.Predicate;

/**
 * This is a generic class for handling SYNC and ASYNC flows.
 *
 * @param <T>
 */
abstract public class TransactionCommand<T> extends HystrixCommand<T> {

    private String transactionName;

    private T request;

    public abstract Predicate<T> decisionHook();

    private int timeout;

    protected TransactionCommand(String transactionName, int timeout, T request) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"), timeout);
        this.transactionName = transactionName;
        this.timeout = timeout;
        this.request = request;
    }

    protected T getRequest() {
        return request;
    }

    @Data
    static class ExecutionResponse<T> {
        Future<T> futureResponse;
        T response;
    }

    /**
     * This method submit transaction based on sync and async flow.
     *
     * @param transaction   transaction
     * @return              Execution respomnse
     */
    protected ExecutionResponse<T> submitTransaction(TransactionCommand<T> transaction) {
        ExecutionResponse<T> executionResponse = new ExecutionResponse<>();
        if(decisionHook().test(getRequest())){
            System.out.println("This is ASYNC flow....");
            Future<T> future = transaction.queue();
            executionResponse.setFutureResponse(future);
        }
        else {
            System.out.println("This is SYNC flow.....");
            executionResponse.setResponse(transaction.execute());
        }
        return executionResponse;
    }
}
