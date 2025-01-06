/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.devtools.livereload;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * A Servlet filter that appends LiveReload.js script to web pages.
 *
 * @author Vedran Pavic
 * @since 4.1.0
 */
public class LiveReloadScriptInjectingFilter extends OncePerRequestFilter {

	private static final Pattern htmlHeadTagsPattern = Pattern
		.compile("<html(?![^>]*/>)[^>]*>(\\s+)?(<head(?![^>]*/>)[^>]*>)?", Pattern.CASE_INSENSITIVE);

	private final String scriptElement;

	public LiveReloadScriptInjectingFilter(int liveReloadPort) {
		this.scriptElement = String.format("<script src=\"/livereload.js?port=%d\"></script>", liveReloadPort);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletResponse responseToUse = response;
		if (shouldWrapRequest(request)) {
			responseToUse = new ContentCachingResponseWrapper(response);
		}
		filterChain.doFilter(request, responseToUse);
		if (responseToUse instanceof ContentCachingResponseWrapper responseWrapper) {
			if (shouldInjectScript(responseToUse)) {
				String content = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
				String modifiedContent = htmlHeadTagsPattern.matcher(content)
					.replaceFirst((matchResult) -> matchResult.group() + this.scriptElement);
				if (!modifiedContent.equals(content)) {
					response.setContentLength(modifiedContent.length());
					response.getWriter().write(modifiedContent);
					return;
				}
			}
			responseWrapper.copyBodyToResponse();
		}
	}

	private boolean shouldWrapRequest(HttpServletRequest request) {
		String contentType = request.getHeader(HttpHeaders.ACCEPT);
		return (contentType != null)
				&& MediaType.parseMediaTypes(contentType).stream().anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
	}

	private boolean shouldInjectScript(HttpServletResponse response) {
		String contentType = response.getContentType();
		return (contentType != null) && MediaType.parseMediaType(contentType).isCompatibleWith(MediaType.TEXT_HTML);
	}

}
