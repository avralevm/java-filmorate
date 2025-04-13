package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {
    public void addLike(Long userId, Long filmId);

    public void removeLike(Long userId, Long filmId);
}