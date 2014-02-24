<div class="twt-border" style="margin-bottom: 20px !important;">
	<blockquote data-twt-id="${status.id}" data-twt-intents="false" data-twt-product="tweetembed" class="twt-o twt-tweet h-entry twt-always-show-actions twt-pinned twt-standard">
	<div class="h-card p-author">      
		<a class="screen-name u-url" href="https://twitter.com/${status.screen_name}" data-screen-name="${status.screen_name}">
			<span class="avatar">
				<img src="${status.profile_image_url}" class="u-photo" alt=""></span>
			<span class="p-name">${status.name}</span>
			<span class="p-nickname">@<b>${status.screen_name}</b></span>
		</a>
        
		<iframe class="twt-follow-button" allowtransparency="true" frameborder="0" scrolling="no" src="//platform.twitter.com/widgets/follow_button.html#align=right&amp;button=grey&amp;screen_name=${status.screen_name}&amp;show_count=false&amp;show_screen_name=false&amp;lang=en"></iframe>
	</div>

	<div class="e-content">      
		<p class="p-name">
		${status.text.encodeAsHTML()}
		</p>      
	</div>
	<div class="footer">
		<a class="view-details" href="https://twitter.com/${status.screen_name}/statuses/${status.id}"><span class="dt-updated " title="${status.created_at}">
			<span class="value-title" title="${status.created_at}"></span>
			${status.created_at}
			</span>
		</a>
		<ul class="twt-actions">
  
			<li><a href="https://twitter.com/intent/tweet?in_reply_to=${status.id}" class="reply-action twt-intent" title="Reply"><i></i><b>Reply</b></a></li>
			<li><a href="https://twitter.com/intent/retweet?tweet_id=${status.id}" class=" retweet-action  twt-intent" title="Retweet"><i></i><b>Retweet</b></a></li>
			<li><a href="https://twitter.com/intent/favorite?tweet_id=${status.id}" class=" favorite-action  twt-intent" title="Favorite"><i></i><b>Favorite</b></a></li>
			  
		</ul>
	</div>
	</blockquote>
</div>