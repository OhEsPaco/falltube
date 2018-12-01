CREATE TABLE `video` (
  `videoId` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `etag` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `publishedAt` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `channelId` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `title` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `description` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `channelTitle` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `categoryId` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `defaultAudioLanguage` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `duration` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `dimension` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `definition` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `caption` tinyint(4) DEFAULT NULL,
  `licensedContent` tinyint(4) DEFAULT NULL,
  `projection` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `viewCount` bigint(15) DEFAULT NULL,
  `likeCount` bigint(15) DEFAULT NULL,
  `dislikeCount` bigint(15) DEFAULT NULL,
  `commentsCount` bigint(15) DEFAULT NULL,
  `tags1` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags2` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags3` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags4` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags5` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags6` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags7` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags8` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags9` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `tags10` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`videoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `comments` (
  `commentId` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `videoId` varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `authorName` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `authorUrl` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `comment` varchar(2000) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  PRIMARY KEY (`commentId`),
  KEY `fkVideoId_idx` (`videoId`),
  CONSTRAINT `fkVideoId` FOREIGN KEY (`videoId`) REFERENCES `video` (`videoid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET GLOBAL max_connections = 500;

CREATE TABLE `video` (
  `videoId` varchar(45) NOT NULL,
  `publishedAt` varchar(100) DEFAULT NULL,
  `channelId` varchar(100) DEFAULT NULL,
  `title` varchar(200) DEFAULT NULL,
  `channelTitle` varchar(200) DEFAULT NULL,
  `categoryId` varchar(100) DEFAULT NULL,
  `duration` bigint(15) DEFAULT NULL,
  `definition` varchar(10) DEFAULT NULL,
  `defaultAudioLanguage` tinyint(4) DEFAULT NULL,
  `caption` tinyint(4) DEFAULT NULL,
  `licensedContent` tinyint(4) DEFAULT NULL,
  `viewCount` bigint(15) DEFAULT NULL,
  `likeCount` bigint(15) DEFAULT NULL,
  `dislikeCount` bigint(15) DEFAULT NULL,
  `commentsCount` bigint(15) DEFAULT NULL,
  `socialImpact` bigint(25) DEFAULT NULL,
  `tags` bigint(5) DEFAULT NULL,
  PRIMARY KEY (`videoId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
