package org.example.controller;

import org.example.member.MemberService;
import org.example.member.dto.MemberCreateRequest;
import org.example.member.dto.MemberResponse;
import org.example.member.dto.MemberUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody MemberCreateRequest request){
        memberService.addMember(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll(){
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<MemberResponse> findById(@PathVariable int id){
        return ResponseEntity.ok(memberService.findById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<MemberResponse>> findByName(@PathVariable String name){
        return ResponseEntity.ok(memberService.findByName(name));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<MemberResponse> findByTitle(@PathVariable String email){
        return ResponseEntity.ok(memberService.findByEmail(email));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<MemberResponse>> findAllInactive(){
        return ResponseEntity.ok(memberService.findAllInactive());
    }

    @PutMapping
    public ResponseEntity<Void> update(@RequestBody MemberUpdateRequest request){
        memberService.update(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/activate/{id}")
    public ResponseEntity<Void> activate(@PathVariable int id){
        memberService.activate(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id){
        memberService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}
