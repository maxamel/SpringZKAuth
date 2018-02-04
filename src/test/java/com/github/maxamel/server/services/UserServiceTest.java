package com.github.maxamel.server.services;

import com.github.maxamel.server.domain.model.User;
import com.github.maxamel.server.domain.model.types.SessionStatus;
import com.github.maxamel.server.domain.repositories.UserRepository;
import com.github.maxamel.server.services.UserService;
import com.github.maxamel.server.services.impl.KafkaProduceServiceImpl;
import com.github.maxamel.server.services.impl.UserServiceImpl;
import com.github.maxamel.server.services.mapping.MappingBasePackage;
import com.github.maxamel.server.web.dtos.UserDto;
import com.github.rozidan.springboot.modelmapper.WithModelMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

@RunWith(SpringRunner.class)
@WithModelMapper(basePackageClasses = MappingBasePackage.class)
@ContextConfiguration(classes = UserServiceImpl.class, initializers = ConfigFileApplicationContextInitializer.class)

public class UserServiceTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;
    
    @MockBean
    private ScheduleTaskService scheduler;
    
    @Mock
    private KafkaProduceServiceImpl kafka;

    @Test
    public void fetchWaitToValidate()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless("a3166e7b31408c5a5c002d86b26069b69dbb91683b180c1a83533b5ea3e7c612744174a5fb9dc604e3b5c5310de40830e9cf6a61fdc71c2732cd49f18728c7961ea6e054c9c5285b8c7d4d79d3ef1fde61172dc03e69a9daf77dc77de71bc11aa05cc15f4cd007fdc72bc5bb5b4475f0d4e0c84ed059692ab4766fa1400adae5bdc3f3b9dcc0ab5af8e00924df40ed9878ed11818059005f31551323a12eece1e0ecb031ff1201093cc632ec23951420298fd4f07f560fd270eaa2ba4cf688a7f2c444a72caee47e35edd42faefabc7f596c150cc6a826eac77d94e1c0d82a28364260c678831178c8d85302f0f6d1be1ec5f99a2ac61d47a6072bb415635606")
                .secret("68566D5971337436763979244226452948404D635166546A576E5A7234753778")
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        UserDto dto = service.fetch("John", "1357118099208700368057074407633646596383265634565817441322774393623597414394421842139088174540908661449333787919910206213141809109604625571630599765212661891148085701263683900613944161466356908696445925362994350641580306372340499894972171648878159229715824673546138156092456135816881867757428587148715197849470096718241278293154865951091942868588343340591942158166002485377540188238462054111356889129468076311709323377114534883018407569143379681131134166943870301882342218168464399099937038878889630569344125000704940858829283581403901919844100622216626934541017662416314902374797221348317924607266677347117504727200");
        assertTrue(dto.getSstatus().equals(SessionStatus.VALIDATED));
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchAuthenticationError()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless("a3166e7b31408c5a5c002d86b26069b69dbb91683b180c1a83533b5ea3e7c612744174a5fb9dc604e3b5c5310de40830e9cf6a61fdc71c2732cd49f18728c7961ea6e054c9c5285b8c7d4d79d3ef1fde61172dc03e69a9daf77dc77de71bc11aa05cc15f4cd007fdc72bc5bb5b4475f0d4e0c84ed059692ab4766fa1400adae5bdc3f3b9dcc0ab5af8e00924df40ed9878ed11818059005f31551323a12eece1e0ecb031ff1201093cc632ec23951420298fd4f07f560fd270eaa2ba4cf688a7f2c444a72caee47e35edd42faefabc7f596c150cc6a826eac77d94e1c0d82a28364260c678831178c8d85302f0f6d1be1ec5f99a2ac61d47a6072bb415635606")
                .secret("68566D5971337436763979244226452948404D635166546A576E5A7234753771")
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch("John", "1357118099208700368057074407633646596383265634565817441322774393623597414394421842139088174540908661449333787919910206213141809109604625571630599765212661891148085701263683900613944161466356908696445925362994350641580306372340499894972171648878159229715824673546138156092456135816881867757428587148715197849470096718241278293154865951091942868588343340591942158166002485377540188238462054111356889129468076311709323377114534883018407569143379681131134166943870301882342218168464399099937038878889630569344125000704940858829283581403901919844100622216626934541017662416314902374797221348317924607266677347117504727200");
    }
    
    @Test(expected = AccessDeniedException.class)
    public void fetchNoSecret()
    {
        User result = User.builder()
                .id(1L)
                .name("John")
                .passwordless("a3166e7b31408c5a5c002d86b26069b69dbb91683b180c1a83533b5ea3e7c612744174a5fb9dc604e3b5c5310de40830e9cf6a61fdc71c2732cd49f18728c7961ea6e054c9c5285b8c7d4d79d3ef1fde61172dc03e69a9daf77dc77de71bc11aa05cc15f4cd007fdc72bc5bb5b4475f0d4e0c84ed059692ab4766fa1400adae5bdc3f3b9dcc0ab5af8e00924df40ed9878ed11818059005f31551323a12eece1e0ecb031ff1201093cc632ec23951420298fd4f07f560fd270eaa2ba4cf688a7f2c444a72caee47e35edd42faefabc7f596c150cc6a826eac77d94e1c0d82a28364260c678831178c8d85302f0f6d1be1ec5f99a2ac61d47a6072bb415635606")
                .secret(null)
                .sstatus(SessionStatus.WAITING)
                .build();
        Optional<User> opt = Optional.of(result);
        
        when(repository.findByName(any(String.class))).thenReturn(opt);
        service.fetch("John", "1357118099208700368057074407633646596383265634565817441322774393623597414394421842139088174540908661449333787919910206213141809109604625571630599765212661891148085701263683900613944161466356908696445925362994350641580306372340499894972171648878159229715824673546138156092456135816881867757428587148715197849470096718241278293154865951091942868588343340591942158166002485377540188238462054111356889129468076311709323377114534883018407569143379681131134166943870301882342218168464399099937038878889630569344125000704940858829283581403901919844100622216626934541017662416314902374797221348317924607266677347117504727200");
    }
}
