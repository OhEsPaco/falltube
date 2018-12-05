/*
MIT License

Copyright (c) 2018 OhEsPaco

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.vaporware.com.domain.utilities;

import org.vaporware.com.domain.objects.SimplifiedVideo;
import java.util.ArrayList;
import org.vaporware.com.domain.video.Items;
import org.vaporware.com.domain.video.Snippet;
import org.vaporware.com.domain.video.YoutubeVideoData;

public class VideoSimplifier {

    public static SimplifiedVideo simplify(YoutubeVideoData yvd) {
        SimplifiedVideo simplificado = new SimplifiedVideo();
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
            if(snp.getTags()!=null){
                for (String tag : snp.getTags()) {
                tags.add(tag);
            }
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

            
             if(item.getStatistics().getViewCount()==null){
                simplificado.setViewCount(0);
            }  else{
               simplificado.setViewCount(Long.parseLong(item.getStatistics().getViewCount()));
            }  
             
             
             
            
             if(item.getStatistics().getLikeCount()==null){
                simplificado.setLikeCount(0);
            }  else{
               simplificado.setLikeCount(Long.parseLong(item.getStatistics().getLikeCount()));
            }  
             
            
             if(item.getStatistics().getDislikeCount()==null){
                simplificado.setDislikeCount(0);
            }  else{
               simplificado.setDislikeCount(Long.parseLong(item.getStatistics().getDislikeCount()));
            }  
           
            
            if(item.getStatistics().getCommentCount()==null){
                simplificado.setCommentCount(0);
            }  else{
                simplificado.setCommentCount(Long.parseLong(item.getStatistics().getCommentCount()));
            }  
            

        }

        return simplificado;
    }
}
