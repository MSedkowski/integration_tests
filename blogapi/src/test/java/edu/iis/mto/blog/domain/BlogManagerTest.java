package edu.iis.mto.blog.domain;

import edu.iis.mto.blog.api.request.PostRequest;
import edu.iis.mto.blog.domain.errors.DomainError;
import edu.iis.mto.blog.domain.model.BlogPost;
import edu.iis.mto.blog.domain.model.LikePost;
import edu.iis.mto.blog.domain.repository.BlogPostRepository;
import edu.iis.mto.blog.domain.repository.LikePostRepository;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.iis.mto.blog.api.request.UserRequest;
import edu.iis.mto.blog.domain.model.AccountStatus;
import edu.iis.mto.blog.domain.model.User;
import edu.iis.mto.blog.domain.repository.UserRepository;
import edu.iis.mto.blog.mapper.DataMapper;
import edu.iis.mto.blog.services.BlogService;

import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogManagerTest {

    @MockBean
    UserRepository userRepository;

    @MockBean
    LikePostRepository likeRepository;

    @MockBean
    BlogPostRepository blogRepository;

    @Autowired
    DataMapper dataMapper;

    @Autowired
    BlogService blogService;

    private User owner;
    private User user;
    private BlogPost post;

    @Before
    public void setup() {
        long ownerId = 1L;
        long userId = 2L;
        long postId = 666L;

        owner = new User();
        owner.setId(ownerId);
        owner.setAccountStatus(AccountStatus.CONFIRMED);

        user = new User();
        user.setId(userId);
        user.setAccountStatus(AccountStatus.CONFIRMED);

        post = new BlogPost();
        post.setUser(owner);
        post.setId(postId);

        Mockito.when(userRepository.findOne(ownerId)).thenReturn(owner);
        Mockito.when(userRepository.findOne(userId)).thenReturn(user);
        Mockito.when(blogRepository.findOne(postId)).thenReturn(post);
        Mockito.when(likeRepository.findByUserAndPost(user, post)).thenReturn(java.util.Optional.of(new LikePost()));
    }

    @Test
    public void creatingNewUserShouldSetAccountStatusToNEW() {
        blogService.createUser(new UserRequest("John", "Steward", "john@domain.com"));
        ArgumentCaptor<User> userParam = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userParam.capture());
        User user = userParam.getValue();
        Assert.assertThat(user.getAccountStatus(), Matchers.equalTo(AccountStatus.NEW));
    }

    @Test
    public void confirmedUserCanLikeOtherUsersBlogPost() {
        blogService.addLikeToPost(user.getId(), post.getId());
    }

    @Test(expected = DomainError.class)
    public void newUserCanNotLikeBlogPost() {
        user.setAccountStatus(AccountStatus.NEW);
        blogService.addLikeToPost(user.getId(), post.getId());
    }

    @Test(expected = DomainError.class)
    public void deletedUserCanNotLikeBlogPost() {
        user.setAccountStatus(AccountStatus.REMOVED);
        blogService.addLikeToPost(user.getId(), post.getId());
    }

    @Test(expected = DomainError.class)
    public void confirmedUserCanNotLikeOwnBlogPost() {
        blogService.addLikeToPost(owner.getId(), post.getId());
    }

}
