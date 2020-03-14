package org.test.syncasync.flow;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class User {

    private Integer id;
    private Integer age;
    private String gender;
    private String firstName;
    private String lastName;
}
