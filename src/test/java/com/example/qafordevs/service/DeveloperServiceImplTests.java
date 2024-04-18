package com.example.qafordevs.service;

import com.example.qafordevs.entity.DeveloperEntity;
import com.example.qafordevs.exception.DeveloperNotFoundException;
import com.example.qafordevs.exception.DeveloperWithDuplicateException;
import com.example.qafordevs.repository.DeveloperRepository;
import com.example.qafordevs.util.DataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeveloperServiceImplTests {

    @Mock
    private DeveloperRepository developerRepository;

    @InjectMocks
    private DeveloperServiceImpl serviceUnderTest;

    @Test
    @DisplayName("Test save developer functionality")
    public void givenDeveloperToSave_whenSaveDeveloper_thenRepositoryIsCalled(){
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(null);
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class)))
                .willReturn(DataUtils.getJohnDoePersisted());
        //when
        DeveloperEntity savedDeveloper = serviceUnderTest.saveDeveloper(developerToSave);
        //then
        assertThat(savedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test save developer with duplicate email functionality")
    public void givenDeveloperToSaveWithDuplicatedEmail_whenSaveDeveloper_thenExceptionIsThrown(){
        //given
        DeveloperEntity developerToSave = DataUtils.getJohnDoeTransient();
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(DataUtils.getJohnDoeTransient());
        //when
        assertThrows(
            DeveloperWithDuplicateException.class, () -> serviceUnderTest.saveDeveloper(developerToSave));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test update developer functionality")
    public void givenDeveloperToUpdate_whenUpdateDeveloper_thenRepositoryIsCalled(){
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt()))
                .willReturn(true);
        BDDMockito.given(developerRepository.save(any(DeveloperEntity.class)))
                .willReturn(developerToUpdate);
        //when
        DeveloperEntity updatedDeveloper = serviceUnderTest.updateDeveloper(developerToUpdate);
        //then
        assertThat(updatedDeveloper).isNotNull();
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test update developer with incorrect id functionality")
    public void givenDeveloperToUpdateIncorrectId_whenUpdateDeveloper_thenExceptionIsThrown(){
        //given
        DeveloperEntity developerToUpdate = DataUtils.getJohnDoePersisted();
        BDDMockito.given(developerRepository.existsById(anyInt()))
                .willReturn(false);
        //when
        assertThrows(
                DeveloperNotFoundException.class, () -> serviceUnderTest.updateDeveloper(developerToUpdate)
        );
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenId_whenGetById_thenDeveloperIsReturned(){
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperById(1);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by id functionality")
    public void givenIncorrectId_whenGetById_thenExceptionIsThrow(){
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperById(1));
        //then
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    public void givenEmail_whenGetDeveloperByEmail_thenDeveloperIsReturned(){
        //given
        String email = "johndoe@mail.com";
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willReturn(DataUtils.getJohnDoePersisted());
        //when
        DeveloperEntity obtainedDeveloper = serviceUnderTest.getDeveloperByEmail(email);
        //then
        assertThat(obtainedDeveloper).isNotNull();
    }

    @Test
    @DisplayName("Test get developer by email functionality")
    public void givenIncorrectEmail_whenGetDeveloperByEmail_thenExceptionIsThrow(){
        //given
        String email = "johndoe@mail.com";
        BDDMockito.given(developerRepository.findByEmail(anyString()))
                .willThrow(DeveloperNotFoundException.class);
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.getDeveloperByEmail(email));
        //then
    }

    @Test
    @DisplayName("Test get all only active developers functionality")
    public void givenThreeDevelopers_whenGetAll_thenOnlyActiveAreReturned(){
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();
        DeveloperEntity developer3 = DataUtils.getFrankJonesPersisted();

        List<DeveloperEntity> developers = List.of(developer1, developer2, developer3);
        BDDMockito.given(developerRepository.findAll())
                .willReturn(developers);
        //when
        List<DeveloperEntity> obtainedDevelopers = serviceUnderTest.getAllDevelopers();
        //then
        assertThat(CollectionUtils.isEmpty(obtainedDevelopers)).isFalse();
        assertThat(obtainedDevelopers.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("Test get all active by specialty developers functionality")
    public void givenThreeDeveloperAndTwoActive_whenGetAllActiveBySpeciality_thenDevelopersAreReturned(){
        //given
        DeveloperEntity developer1 = DataUtils.getJohnDoePersisted();
        DeveloperEntity developer2 = DataUtils.getMikeSmithPersisted();

        List<DeveloperEntity> developers = List.of(developer1, developer2);
        BDDMockito.given(developerRepository.findAllActiveBySpeciality(anyString()))
                .willReturn(developers);
        //when
        List<DeveloperEntity> obtainedDevelopers = serviceUnderTest.getAllActiveBySpecialty("Java");
        //then
        assertThat(CollectionUtils.isEmpty(obtainedDevelopers)).isFalse();
        assertThat(obtainedDevelopers.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenId_thenSoftDeleteById_whenRepositorySaveMethodIsCalled(){
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.softDeleteById(1);
        //then
        verify(developerRepository, times(1)).save(any(DeveloperEntity.class));
        verify(developerRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Test soft delete by id functionality")
    public void givenIncorrectId_thenSoftDeleteById_whenExceptionIsThrown(){
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.softDeleteById(1));
        //then
        verify(developerRepository, never()).save(any(DeveloperEntity.class));
    }

    @Test
    @DisplayName("Test hard delete by id functionality")
    public void givenCorrectId_whenHardDeleteById_thenDeleteRepoMethodIsCalled() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.of(DataUtils.getJohnDoePersisted()));
        //when
        serviceUnderTest.hardDeleteById(1);
        //then
        verify(developerRepository, times(1)).deleteById(anyInt());
    }


    @Test
    @DisplayName("Test hard delete by id functionality")
    public void givenIncorrectId_whenHardDeleteById_thenExceptionIsThrown() {
        //given
        BDDMockito.given(developerRepository.findById(anyInt()))
                .willReturn(Optional.empty());
        //when
        assertThrows(DeveloperNotFoundException.class, () -> serviceUnderTest.hardDeleteById(1));
        //then
        verify(developerRepository, never()).deleteById(anyInt());
    }

}
