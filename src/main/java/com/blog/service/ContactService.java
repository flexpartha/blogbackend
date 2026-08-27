package com.blog.service;

import com.blog.entity.Contact;
import com.blog.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }
}
