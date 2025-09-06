package com.example.musicplayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FavoriteUnitTest {

    private lateinit var testSong1: Music
    private lateinit var testSong2: Music

    @Before
    fun setup() {
        // Reset favorites before each test
        FavoriteActivity.favoriteSongs.clear()

        // Dummy songs
        testSong1 = Music("1", "Song One", "Album A", "Artist A", 200000L, "uri://song1", "uri://art1")
        testSong2 = Music("2", "Song Two", "Album B", "Artist B", 180000L, "uri://song2", "uri://art2")
    }

    //run each test sequentially
    @Test
    fun testAddFavoriteSong() {
        // Add song to favorites
        FavoriteActivity.favoriteSongs.add(testSong1)

        // check if it was added
        assertEquals(1, FavoriteActivity.favoriteSongs.size)
        assertEquals("Song One", FavoriteActivity.favoriteSongs[0].title)
    }

    @Test
    fun testRemoveFavoriteSong() {
        // Add and then remove
        FavoriteActivity.favoriteSongs.add(testSong1)
        FavoriteActivity.favoriteSongs.remove(testSong1)

        // check empty
        assertTrue(FavoriteActivity.favoriteSongs.isEmpty())
    }
    @Test
    fun testMultipleFavorites() {
        // Add two songs
        FavoriteActivity.favoriteSongs.add(testSong1)
        FavoriteActivity.favoriteSongs.add(testSong2)

        assertEquals(2, FavoriteActivity.favoriteSongs.size)
        assertEquals("Song Two", FavoriteActivity.favoriteSongs[1].title)
    }

    @Test
    fun testFavoriteCheckerFound() {
        FavoriteActivity.favoriteSongs.add(testSong1)

        val checker = FavoriteActivity().favoriteChecker("1")

        // Should find the first song
        assertEquals(0, checker)
        assertTrue(PlayerActivity.isFavorite)
    }

    //test should fail
    @Test
    fun testFavoriteCheckerNotFound() {
        FavoriteActivity.favoriteSongs.add(testSong1)

        val checker = FavoriteActivity().favoriteChecker("99")

        // Should not find
        assertEquals(-1, checker)
        assertFalse(PlayerActivity.isFavorite)
    }

}