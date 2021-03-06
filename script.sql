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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;