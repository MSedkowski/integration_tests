package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository repository;

    private User user;

    @Before
    public void setUp() {
        user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jkowalski@domain.com");
        user.setAccountStatus(AccountStatus.NEW);
    }

    @Test
    public void shouldFindNoUsersIfRepositoryIsEmpty() {
        repository.deleteAll();
        List<User> users = repository.findAll();
        Assert.assertThat(users, Matchers.hasSize(0));
    }

    @Test
    public void shouldFindOneUsersIfRepositoryContainsOneUserEntity() {
        User persistedUser = entityManager.find(User.class, 1L);
        List<User> users = repository.findAll();
        Assert.assertThat(users, Matchers.hasSize(1));
        Assert.assertThat(users.get(0).getEmail(), Matchers.equalTo(persistedUser.getEmail()));
    }

    @Test
    public void shouldStoreANewUser() {
        User user2 = new User();
        user2.setFirstName("Tom");
        user2.setEmail("tom@domain.com");
        user2.setAccountStatus(AccountStatus.NEW);
        User persistedUser = repository.save(user2);

        Assert.assertThat(persistedUser.getId(), Matchers.notNullValue());
        repository.deleteAll();
    }

    @Test
    public void shouldFindUserByFirstName() {
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("john", " ", " ")
                .size(), Matchers.equalTo(1));
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("John", " ", " ")
                .size(), Matchers.equalTo(1));
    }

    @Test
    public void shouldFindUserByLastName() {
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", "steward", " ")
                .size(), Matchers.equalTo(1));
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", "Steward", " ")
                .size(), Matchers.equalTo(1));
    }

    @Test
    public void shouldFindUserByEmail() {
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", " ", "john@domain.com")
                .size(), Matchers.equalTo(1));
    }

    @Test
    public void shouldFindUsersByFirstNameLastNameOrEmail() {
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("John", "Steward", "john@domain.com")
                .size(), Matchers.equalTo(1));
    }

    @Test
    public void shouldNotFindUserWhenParamsAreIncorrect() {
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", "Nowak", " ")
                .size(), Matchers.equalTo(0));
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase("Marian", " ", " ")
                .size(), Matchers.equalTo(0));
        Assert.assertThat(repository
                .findByFirstNameContainingOrLastNameContainingOrEmailContainingAllIgnoreCase(" ", " ", "test@wp.pl")
                .size(), Matchers.equalTo(0));
    }


}
