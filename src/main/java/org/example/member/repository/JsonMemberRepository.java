package org.example.member.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.member.Member;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class JsonMemberRepository implements MemberRepository {

    private final ObjectMapper mapper;
    private final String filePath;
    private final List<Member> members;
    private int availableId;

    public JsonMemberRepository(ObjectMapper mapper, String filePath) {
        this.mapper = mapper;
        this.filePath = filePath;
        this.members = new ArrayList<>();
        this.availableId = 0;
    }

    @Override
    public void save(Member member) {
        member.setId(getAvailableId());
        members.add(member);
    }

    @Override
    public Optional<Member> findById(int id) {
        return members.stream()
                .filter(member -> member.getId() == id)
                .findFirst();
    }

    @Override
    public List<Member> findByName(String name) {
        return members.stream()
                .filter(Member::isActive)
                .filter(member -> member
                        .getName()
                        .equals(name))
                .toList();
    }

    @Override
    public List<Member> findAll() {
        return members.stream()
                .filter(Member::isActive)
                .toList();
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return members.stream()
                .filter(Member::isActive)
                .filter(member -> member.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public void update(Member member) {

    }

    @Override
    public void deactivate(int id) {

    }

    @Override
    public void activate(int id) {

    }


    @Override
    public List<Member> findInactiveMembers() {
        return members.stream()
                .filter(member -> !member.isActive())
                .toList();
    }


    @PostConstruct
    public void load(){
        try{
            File file = new File(filePath);
            if(!file.exists()){
                return;
            }

            List<Member> loaded = mapper.readValue(
                    file,
                    new TypeReference<List<Member>>(){}
            );

            members.addAll(loaded);
            availableId = members.stream()
                    .mapToInt(Member::getId)
                    .max()
                    .orElse(0);
        }catch (IOException e){
            System.out.println("Could not load json file! " + e.getMessage());
        }
    }

    @PreDestroy
    public void saveEnd(){
        try{
            File file = new File(filePath);
            mapper.writeValue(file, members);
        }catch (IOException e){
            System.out.println("Could not save json file! " + e.getMessage());
        }
    }

    private int getAvailableId() {
        return ++availableId;
    }
}
