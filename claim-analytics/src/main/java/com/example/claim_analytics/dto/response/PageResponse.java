package com.example.claim_analytics.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

	private List<T> data;
	private long total;
	private int limit;
	private int offset;

	public static <T> PageResponse<T> from(Page<T> page) {
		return PageResponse.<T>builder()
				.data(page.getContent())
				.total(page.getTotalElements())
				.limit(page.getSize())
				.offset(page.getNumber() * page.getSize())
				.build();
	}
}