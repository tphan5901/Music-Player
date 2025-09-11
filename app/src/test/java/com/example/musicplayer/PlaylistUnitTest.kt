package com.example.musicplayer

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class PlaylistUnitTest {
    private var testPlaylist: Playlist? = null
    private var testSong1: Music? = null
    private var testSong2: Music? = null

    @Before
    fun setup() {
        // Initialize dummy playlist
        testPlaylist = Playlist("Test Playlist", ArrayList<Music>(), "01-01-2025")

        // Attach playlist to global ref (simulates PlaylistActivity)
        PlaylistActivity.musicPlaylist = MusicPlaylist()
        PlaylistActivity.musicPlaylist.ref.add(testPlaylist!!)

        PlaylistDetails.currentPlaylistPos = 0

        // dummy songs
        testSong1 = Music("1", "Song One", "Album A", "Artist A", 200000L, "uri://song1", "uri://art1")
        testSong2 = Music("2", "Song Two", "Album B", "Artist B", 180000L, "uri://song2", "uri://art2")
    }

    @Test
    fun testAddSongToPlaylist() {
        // Initially empty
        Assert.assertEquals(0, PlaylistActivity.musicPlaylist.ref[0].playlist.size.toLong())

        // Add song
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong1!!)

        // Verify size increased
        Assert.assertEquals(1, PlaylistActivity.musicPlaylist.ref[0].playlist.size.toLong())
        Assert.assertEquals("Song One", PlaylistActivity.musicPlaylist.ref[0].playlist[0].title)
    }

    @Test
    fun testPreventDuplicateSong() {
        // Add the same song twice
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong1!!)
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong1!!)

        // Should contain duplicates (adapter.addSong() prevents duplicates, but raw add() doesn’t)
        Assert.assertEquals(2, PlaylistActivity.musicPlaylist.ref[0].playlist.size.toLong())
    }

    @Test
    fun testMultipleSongsAdded() {
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong1!!)
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong2!!)

        Assert.assertEquals(2, PlaylistActivity.musicPlaylist.ref[0].playlist.size.toLong())
        Assert.assertEquals("Song Two", PlaylistActivity.musicPlaylist.ref[0].playlist[1].title)
    }

    @Test
    fun testRemoveSongFromPlaylist() {
        // Add two songs
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong1!!)
        PlaylistActivity.musicPlaylist.ref[0].playlist.add(testSong2!!)

        // Remove first song
        PlaylistActivity.musicPlaylist.ref[0].playlist.remove(testSong1!!)

        // Verify removal
        Assert.assertEquals(1, PlaylistActivity.musicPlaylist.ref[0].playlist.size.toLong())
        Assert.assertEquals("Song Two", PlaylistActivity.musicPlaylist.ref[0].playlist[0].title)
    }

    @Test
    fun testAddPlaylistFromActivity() {
        val activity = PlaylistActivity()

        // Reset before test
        PlaylistActivity.musicPlaylist = MusicPlaylist()
        Assert.assertEquals(0, PlaylistActivity.musicPlaylist.ref.size)

        // Call addPlaylist method
        activity.addPlaylist("Workout Mix")

        // Verify
        Assert.assertEquals(1, PlaylistActivity.musicPlaylist.ref.size)
        Assert.assertEquals("Workout Mix", PlaylistActivity.musicPlaylist.ref[0].name)
    }

    @Test
    fun testCreateNewPlaylist() {
        // check if there are no playlists at the start
        PlaylistActivity.musicPlaylist = MusicPlaylist()
        Assert.assertEquals(0, PlaylistActivity.musicPlaylist.ref.size)

        // Create new playlist object
        val newPlaylist = Playlist("ignored", ArrayList(), "05-09-2025", "")
        newPlaylist.name = "My Playlist"
        PlaylistActivity.musicPlaylist.ref.add(newPlaylist)


        // Verify playlist is created
        Assert.assertEquals(1, PlaylistActivity.musicPlaylist.ref.size)
        Assert.assertEquals("My Playlist", PlaylistActivity.musicPlaylist.ref[0].name)
    }


}