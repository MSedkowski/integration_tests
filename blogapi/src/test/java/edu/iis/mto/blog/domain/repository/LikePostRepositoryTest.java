package edu.iis.mto.blog.domain.repository;

import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class LikePostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LikePostRepository likePostRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BlogPostRepository blogPostRepository;

    private LikePost likePost;
    private BlogPost blogPost;
    private User user;

    @Before
    public void setup() {
        user = new User();
        user.setFirstName("Jan");
        user.setLastName("Kowalski");
        user.setEmail("jan@kowalski.pl");
        user.setAccountStatus(AccountStatus.NEW);
        userRepository.save(user);

        blogPost = new BlogPost();
        blogPost.setUser(user);
        blogPost.setEntry("entry");
        blogPostRepository.save(blogPost);

        likePost = new LikePost();
        likePost.setPost(blogPost);
        likePost.setUser(user);
        likePostRepository.save(likePost);
    }

    @Test
    public void shouldCorrectlyCreateLikePostEntity() {
        Assert.assertThat(likePostRepository.findAll(), Matchers.hasSize(1));
        Assert.assertThat(likePostRepository.findAll().get(0).getPost(), Matchers.equalTo(blogPost));
        Assert.assertThat(likePostRepository.findAll().get(0).getUser(), Matchers.equalTo(user));
    }

    @Test
    public void shouldCorrectlyModifyLikePostEntity() {
        User user2 = user;
        user2.setFirstName("Marian");
        userRepository.save(user2);

        BlogPost blogPost2 = new BlogPost();
        blogPost2.setUser(user2);
        blogPost2.setEntry("Entry 2");
        blogPostRepository.save(blogPost2);

        LikePost likePost2 = likePostRepository.findAll().get(0);
        likePost2.setPost(blogPost2);
        likePostRepository.save(likePost2);

        Assert.assertThat(likePostRepository.findAll(), Matchers.hasSize(1));
        Assert.assertThat(likePostRepository.findAll().get(0).getPost(), Matchers.equalTo(blogPost2));
        Assert.assertThat(likePostRepository.findAll().get(0).getUser(), Matchers.equalTo(user2));
    }

    @Test
    public void shouldCorrectlyDeleteLikePostEntities() {
        likePostRepository.deleteAll();
        Assert.assertThat(likePostRepository.findAll(), Matchers.hasSize(0));
    }

    @Test
    public void shouldCorrectlyFindEntityByUserAndPost() {
        Assert.assertThat(likePostRepository.findByUserAndPost(user, blogPost).get(), Matchers.equalTo(likePost));
    }
}