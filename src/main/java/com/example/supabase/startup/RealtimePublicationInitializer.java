package com.example.supabase.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class RealtimePublicationInitializer {

    private static final Logger log = LoggerFactory.getLogger(RealtimePublicationInitializer.class);
    private final JdbcTemplate jdbcTemplate;

    public RealtimePublicationInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 앱이 완전히 올라온 뒤(JPA 테이블 생성 완료 후) 실행.
     * publication에 public.rooms가 없으면 자동으로 추가한다 (idempotent).
     */
    @EventListener(ApplicationReadyEvent.class)
    public void ensureRealtimePublication() {
        String sql = """
            do $$
            begin
              -- supabase_realtime publication에 public.rooms가 없으면 추가
              if not exists (
                select 1
                from pg_publication_tables
                where pubname   = 'supabase_realtime'
                  and schemaname = 'public'
                  and tablename  = 'rooms'
              ) then
                execute 'alter publication supabase_realtime add table public.rooms';
              end if;
            end $$;
            """;
        try {
            jdbcTemplate.execute(sql);
            log.info("✅ Ensured: public.rooms is attached to publication supabase_realtime");
        } catch (Exception e) {
            log.error("❌ Failed to ensure realtime publication for public.rooms", e);
        }
    }
}