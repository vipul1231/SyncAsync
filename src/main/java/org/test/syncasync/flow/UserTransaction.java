package org.test.syncasync.flow;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;

@Slf4j
public class UserTransaction extends TransactionCommand<User> {

    private String transactionName;

    protected UserTransaction(String transactionName, int timeout, User user) {
        super(transactionName, timeout, user);
        this.transactionName = transactionName;
    }

    @Override
    public Predicate<User> decisionHook() {
        return p -> p.getAge() > 21 && p.getGender().equalsIgnoreCase("M");
    }

    protected User getFallback() {
        System.out.println("Request timeout, entering fallback.....");
        return User.builder().firstName("Default FirstName").lastName("Default LastName").build();
    }

    @Override
    protected User run() throws Exception {
        System.out.println("Running for user: "+ getRequest().getFirstName());
        //Thread.sleep(3000);
        return getRequest();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        User user = User.builder().firstName("Vipul").lastName("Tiwari").age(30).gender("M").build();
        //p -> p.getAge() > 21 && p.getGender().equalsIgnoreCase("M")
        UserTransaction userTransaction = new UserTransaction("User transaction",1000, user);
        ExecutionResponse<User> executionResponse = userTransaction.submitTransaction(userTransaction);

        if(executionResponse.getFutureResponse() != null){
            Future<User> future = executionResponse.getFutureResponse();
            System.out.println("Transaction name '"+userTransaction.transactionName+"' returning response from future"+future.get());
        }
        else {
            System.out.println("Transaction name '"+userTransaction.transactionName+"' execution response: "+executionResponse.getResponse());
        }
    }
}
