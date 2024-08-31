package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.utils.IdentifierGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private static final Map<Long, HashMap<Long, Item>> usersItems = new HashMap<>();
    private final IdentifierGenerator identifierGenerator;

    @Override
    public List<Item> findAllByUserId(Long userId) {
        HashMap<Long, Item> items = usersItems.getOrDefault(userId, new HashMap<>());
        return new ArrayList<>(items.values());
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        for (HashMap<Long, Item> map : usersItems.values()) {
            if (map.containsKey(itemId)) {
                return Optional.ofNullable(map.get(itemId));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Item> findByUserIdAndItemId(Long userId, Long itemId) {
        HashMap<Long, Item> items = usersItems.get(userId);
        if (items == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item create(Long userId, Item item) {
        Long itemId = identifierGenerator.getNextId();
        item.setId(itemId);
        HashMap<Long, Item> items = usersItems.getOrDefault(userId, new HashMap<>());
        items.put(item.getId(), item);
        usersItems.put(userId, items);
        return item;
    }

    @Override
    public Item update(Item updatedItem) {
        for (HashMap<Long, Item> map : usersItems.values()) {
            if (map.containsKey(updatedItem.getId())) {
                map.put(updatedItem.getId(), updatedItem);
                break;
            }
        }
        return updatedItem;
    }

    @Override
    public void deleteByUserIdAndItemId(Long userId, Long itemId) {
        HashMap<Long, Item> items = usersItems.get(userId);
        if (items != null) {
            items.remove(itemId);
        }
    }

    @Override
    public List<Item> search(String text) {
        return usersItems.values().stream()
                .map(HashMap::values)
                .flatMap(Collection::stream).filter(item -> {
                    if (!item.getAvailable()) {
                        return false;
                    }
                    return Stream.of(item.getName(), item.getDescription())
                            .anyMatch(value -> value.toLowerCase().contains(text.toLowerCase()));
                })
                .toList();
    }
}
