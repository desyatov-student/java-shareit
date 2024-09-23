package ru.practicum.shareit.request.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "item_requests", schema = "public")
@Getter
@Setter
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id")
    private Collection<Item> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    private String description;

    private Instant created = Instant.now();
}
