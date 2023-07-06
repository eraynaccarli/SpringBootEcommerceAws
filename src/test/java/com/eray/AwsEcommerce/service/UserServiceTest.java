package com.eray.AwsEcommerce.service;

import com.eray.AwsEcommerce.TestSupport;
import com.eray.AwsEcommerce.dto.CreateUserRequest;
import com.eray.AwsEcommerce.dto.UpdateUserRequest;
import com.eray.AwsEcommerce.dto.UserDto;
import com.eray.AwsEcommerce.dto.UserDtoConverter;
import com.eray.AwsEcommerce.exception.UserIsNotActiveException;
import com.eray.AwsEcommerce.exception.UserNotFoundException;
import com.eray.AwsEcommerce.model.User;
import com.eray.AwsEcommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest extends TestSupport {

    private UserDtoConverter converter;
    private UserRepository repository;
    private UserService userService;

    @BeforeEach // her test çalıştığında beforeEach tekrar çalışacak @BeforeAll testten önce bir defa çalışır
    public void setUp(){
        converter = Mockito.mock(UserDtoConverter.class);
        repository = Mockito.mock(UserRepository.class);
        userService = new UserService(repository, converter);
    }

    @Test
    public void testGetAllUsers_itShouldReturnUserDtoList(){
        List<User> userList = generateUsers();
        List<UserDto> userDtoList = generateUserDtoList(userList);

        Mockito.when(repository.findAll()).thenReturn(userList);
        Mockito.when(converter.convert(userList)).thenReturn(userDtoList);

        List<UserDto> result = userService.getAllUsers();

        // aldigim sonuc ile benim bekledigim ayni mi onu karsilastirdim
        assertEquals(userDtoList,result);

        // repository nin findAll methodu çağırıldı mı onu kontrol ettik
        Mockito.verify(repository).findAll();
        Mockito.verify(converter).convert(userList);

    }

    // başarılı senaryo user'ın Mail adresiyle bulunup dto ya cevrilmesi  ikinci senaryo mail adresine ait kayıt bulunmaması
    @Test
    public void testGetUserByMail_whenUserMailExist_itShouldReturnUserDtoList(){
        String mail = "eraynaccarli@outlook.com";
        User user = generateUser(mail);
        UserDto userDto = generateUserDto(mail);

        Mockito.when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        Mockito.when(converter.convert(user)).thenReturn(userDto);

        UserDto result = userService.getUserByMail(mail);


        assertEquals(userDto,result);

        Mockito.verify(repository).findByMail(mail);
        Mockito.verify(converter).convert(user);

    }

    @Test
    public void testGetUserByMail_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException(){
        String mail = "eraynaccarli@outlook.com";

        Mockito.when(repository.findByMail(mail)).thenReturn(Optional.empty());
        Exception exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserByMail(mail));


        Mockito.verify(repository).findByMail(mail);
        Mockito.verifyNoInteractions(converter);

    }

    @Test
    public void testCreateUser_itShouldReturnCreatedUserDto(){
        String mail = "eraynaccarli@outlook.com";

        CreateUserRequest request = new CreateUserRequest(mail,"firstName","lastName", "");
        User user = new User(mail,"firstName","lastName","",false);
        User savedUser  = new User(1L,mail,"firstName","lastName", "");
        UserDto userDto = new UserDto(mail,"firstName","lastName", "");

        // User ı saveledigimizde bize user donecegini soyledik
        Mockito.when(repository.save(user)).thenReturn(savedUser);
        // savedUserı dto ya convert ettigimizde bize userDto donecegini soyledik
        Mockito.when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(request);

        assertEquals(userDto,result);

        Mockito.verify(repository).save(user);
        Mockito.verify(converter).convert(savedUser);

    }

    // update basarili senaryo
    @Test
    public void testUpdateUser_whenUserMailExistAndUserIsActive_itShouldReturnUpdatedUserDto(){
        String mail = "eraynaccarli@outlook.com";

        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2", "");
        User user = new User(1L,mail,"firstName","lastName","",true);
        User updatedUser = new User(1L,mail,"firstName2","lastName2","",true);
        User savedUser  = new User(1L,mail,"firstName2","lastName2", "",true);
        UserDto userDto = new UserDto(mail,"firstName2","lastName2", "");

        // maile gittigin zaman userı dön
        Mockito.when(repository.findByMail(mail)).thenReturn(Optional.of(user));
        Mockito.when(repository.save(updatedUser)).thenReturn(savedUser);
        Mockito.when(converter.convert(savedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(mail,request);

        assertEquals(userDto,result);

        Mockito.verify(repository).findByMail(mail);
        Mockito.verify(repository).save(updatedUser);
        Mockito.verify(converter).convert(savedUser);

    }

    // update edilecek kullanicinin mailinin bulunamadigi senaryo

    @Test
    public void testUpdateUser_whenUserMailDoesNotExist_itShouldThrowUserNotFoundException(){
        String mail = "eraynaccarli@outlook.com";

        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2", "");


        // maile gittigin zaman userı dön
        Mockito.when(repository.findByMail(mail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(mail,request));


        Mockito.verify(repository).findByMail(mail);
        Mockito.verifyNoMoreInteractions(repository); // verify edilmeyen methodlar çağırılmasın
        Mockito.verifyNoInteractions(converter); // convertırı hiç kullanmadıgımız için verifyNoInteractions'ın içine verdik.

    }

    // Update edilecek userın mailinin bulunduğu ama Userın aktif olmadığı senaryo
    @Test
    public void testUpdateUser_whenUserMailExistButUserIsNotActive_itShouldThrowUserNotActiveException(){
        String mail = "eraynaccarli@outlook.com";

        UpdateUserRequest request = new UpdateUserRequest("firstName2","lastName2", "");
        User user = new User(1L,mail,"firstName","lastName","",false);

        Mockito.when(repository.findByMail(mail)).thenReturn(Optional.of(user));

        assertThrows(UserIsNotActiveException.class,
                () -> userService.updateUser(mail,request));


        Mockito.verify(repository).findByMail(mail);
        Mockito.verifyNoMoreInteractions(repository); // verify edilmeyen methodlar çağırılmasın
        Mockito.verifyNoInteractions(converter); // convertırı hiç kullanmadıgımız için verifyNoInteractions'ın içine verdik.

    }

    // Userın delete edildiği başarılı senaryo
    @Test
    public void testDeleteUser_whenUserIdExist_itShouldDeleteUser(){
        User user = new User(userId,"eraynaccarli@outlook.com","firstName","lastName","",false);

        Mockito.when(repository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteUser(userId);

        Mockito.verify(repository).findById(userId);
        Mockito.verify(repository).deleteById(userId);

    }

    // Delete edilecek userın id sinin bulunamadığı senaryo
    @Test
    public void testDeleteUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException(){

        Mockito.when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class ,
                () -> userService.deleteUser(userId));

        Mockito.verify(repository).findById(userId);
        Mockito.verifyNoMoreInteractions(repository);

    }

    // Deactivate olan userın id si mevcut ise activeligini false a çektik
    @Test
    public void testDeactivateUser_whenUserIdExist_itShouldUpdateUserByActiveFalse(){
        String mail = "eraynaccarli@outlook.com";
        User user = new User(userId,mail,"firstName","lastName","",true);
        User savedUser = new User(userId,mail,"firstName","lastName","",false);

        Mockito.when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);


        Mockito.verify(repository).findById(userId);
        Mockito.verify(repository).save(savedUser);
    }

    // deactive hale getirelecek userın id sinin bulunamadıığı senaryo
    @Test
    public void testDeactivateUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException(){

        Mockito.when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class ,
                () -> userService.deactivateUser(userId));

        Mockito.verify(repository).findById(userId);
        Mockito.verifyNoMoreInteractions(repository);

    }

    // Aktif olan kullanıcının id si mevcut ise aktifligini true ya çektiğimiz senaryo
    @Test
    public void testActivateUser_whenUserIdExist_itShouldUpdateUserByActiveTrue(){
        String mail = "eraynaccarli@outlook.com";
        User user = new User(userId,mail,"firstName","lastName","",false);
        User savedUser = new User(userId,mail,"firstName","lastName","",true);

        Mockito.when(repository.findById(userId)).thenReturn(Optional.of(user));

        userService.activateUser(userId);


        Mockito.verify(repository).findById(userId);
        Mockito.verify(repository).save(savedUser);
    }

    // Aktif olan kullanıcının id si bulunamadığı zaman hata atan senaryoyu test ettik
    @Test
    public void testActivateUser_whenUserIdDoesNotExist_itShouldThrowUserNotFoundException(){

        Mockito.when(repository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class ,
                () -> userService.activateUser(userId));

        Mockito.verify(repository).findById(userId);
        Mockito.verifyNoMoreInteractions(repository);

    }



}