package com.example.qafordevs.service;

import com.example.qafordevs.entity.DeveloperEntity;
import com.example.qafordevs.entity.Status;
import com.example.qafordevs.exception.DeveloperNotFoundException;
import com.example.qafordevs.exception.DeveloperWithDuplicateException;
import com.example.qafordevs.repository.DeveloperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService{

    private final DeveloperRepository developerRepository;
    @Override
    public DeveloperEntity saveDeveloper(DeveloperEntity developer) {
        DeveloperEntity duplicateCandidate = developerRepository.findByEmail(developer.getEmail());

        if(Objects.nonNull(duplicateCandidate)){
            throw new DeveloperWithDuplicateException("Developer with defined e,ail is already exist");
        }
        return developerRepository.save(developer);
    }

    @Override
    public DeveloperEntity updateDeveloper(DeveloperEntity developer) {
        boolean isExists = developerRepository.existsById(developer.getId());

        if(!isExists){
            throw new DeveloperNotFoundException("Developer not found");
        }
        return developerRepository.save(developer);
    }

    @Override
    public DeveloperEntity getDeveloperById(Integer id) {
        return developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));
    }

    @Override
    public DeveloperEntity getDeveloperByEmail(String email) {
        DeveloperEntity obtainedDeveloper = developerRepository.findByEmail(email);

        if (Objects.isNull(obtainedDeveloper)){
             throw new DeveloperNotFoundException("Developer not found");
        }
        return obtainedDeveloper;
    }

    @Override
    public List<DeveloperEntity> getAllDevelopers() {
        return developerRepository.findAll()
                .stream().filter(d -> {
                        return d.getStatus().equals(Status.ACTIVE);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DeveloperEntity> getAllActiveBySpecialty(String specialty) {
        return developerRepository.findAllActiveBySpeciality(specialty);
    }

    @Override
    public void softDeleteById(Integer id) {
        DeveloperEntity obtainedDeveloper = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));

        obtainedDeveloper.setStatus(Status.DELETED);
        developerRepository.save(obtainedDeveloper);

    }

    @Override
    public void hardDeleteById(Integer id) {
        DeveloperEntity obtainedDeveloper = developerRepository.findById(id)
                .orElseThrow(() -> new DeveloperNotFoundException("Developer not found"));

        developerRepository.deleteById(obtainedDeveloper.getId());
    }
}
