/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import java.util.ArrayList;
import org.vaporware.com.domain.video.Items;
import org.vaporware.com.domain.video.Snippet;
import org.vaporware.com.domain.video.YoutubeVideoData;

/**
 *
 * @author pacog
 */
public class YvdSimplifier {

    public static YvdSimplified simplify(YoutubeVideoData yvd) {
        YvdSimplified simplificado = new YvdSimplified();
        Items[] items = yvd.getItems();
        for (Items item : items) {
            simplificado.setId(item.getId());
            simplificado.setEtag(item.getEtag());

            Snippet snp = item.getSnippet();

            simplificado.setChannelId(snp.getChannelId());
            simplificado.setPublishedAt(snp.getPublishedAt());
            simplificado.setTitle(snp.getTitle());
            simplificado.setDescription(snp.getDescription());
            simplificado.setChannelTitle(snp.getChannelTitle());

            ArrayList<String> tags = new ArrayList<String>();
            for (String tag : snp.getTags()) {
                tags.add(tag);
            }
            simplificado.setTags(tags);

            simplificado.setCategoryId(snp.getCategoryId());
            simplificado.setDefaultAudioLanguage(snp.getDefaultAudioLanguage());

            simplificado.setDuration(item.getContentDetails().getDuration());
            simplificado.setDimension(item.getContentDetails().getDimension());
            simplificado.setDefinition(item.getContentDetails().getDefinition());
            simplificado.setCaption(Boolean.parseBoolean(item.getContentDetails().getCaption()));
            simplificado.setLicensedContent(Boolean.parseBoolean(item.getContentDetails().getLicensedContent()));
            simplificado.setProjection(item.getContentDetails().getProjection());

            simplificado.setViewCount(Long.parseLong(item.getStatistics().getViewCount()));
            simplificado.setLikeCount(Long.parseLong(item.getStatistics().getLikeCount()));
            simplificado.setDislikeCount(Long.parseLong(item.getStatistics().getDislikeCount()));
            simplificado.setCommentCount(Long.parseLong(item.getStatistics().getCommentCount()));

        }

        return simplificado;
    }
}
