package com.eray.AwsEcommerce;

import com.eray.AwsEcommerce.dto.UserDto;
import com.eray.AwsEcommerce.model.User;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestSupport {

    public static Long userId = 100L;

    // test için user oluşturduk
    public static List<User> generateUsers() {
        return IntStream.range(0, 5).mapToObj(i ->
                    new User((long)i,
                    i + "@eraynacarli.net",
                    "firstName" + i,
                    "lastName" + i,
                    "middleName" + "",
                    new Random(2).nextBoolean())
        ).collect(Collectors.toList());
    }

    // test için user ı dto a convert eden methodu oluşturduk
    public static List<UserDto> generateUserDtoList(List<User> users) {
        return users.stream()
                .map(from -> new UserDto(from.getMail(), from.getFirstName(), from.getLastName(),
                        from.getMiddleName())).collect(Collectors.toList());
    }

    // dışardan mail alıp user ve userdto oluşturduk bunları test içinde kullanacagız.
    public static User generateUser(String mail){
        return  new User((long) userId,
                userId + "@eraynacarli.net",
                "firstName" + userId,
                "lastName" + userId,
                "middleName" + "",
                true);
    }

    public static UserDto generateUserDto(String mail){
     return new UserDto(mail , "firstName" + userId , "lastName" + userId, "");
    }


}
