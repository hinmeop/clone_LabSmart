-- =====================================================================
-- MIGRATION SCRIPT: ADD FULL METADATA COLUMNS TO ALL 31 ENTITIES / TABLES
-- Metadata: created_at, created_by, updated_at, updated_by, status, deleted_at, is_deleted
-- =====================================================================

BEGIN;

DO $$
DECLARE
    tbl text;
    tables text[] := ARRAY[
        'users',
        'auth_verification_codes',
        'system_settings',
        'notification_settings',
        'domains',
        'project_types',
        'projects',
        'project_members',
        'tasks',
        'task_assignees',
        'task_comments',
        'task_logs',
        'task_logworks',
        'news',
        'posts',
        'post_attachments',
        'post_comments',
        'recruitment_posts',
        'recruitment_form_fields',
        'applications',
        'interviews',
        'folders',
        'materials',
        'notifications',
        'report_exports',
        'chat_conversations',
        'chat_messages',
        'chat_participants',
        'chat_message_attachments',
        'chat_message_reactions',
        'chat_message_mentions'
    ];
    fk_created text;
    fk_updated text;
    trg_name text;
BEGIN
    FOREACH tbl IN ARRAY tables LOOP
        -- 1. created_at
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();', tbl);

        -- 2. created_by
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS created_by BIGINT;', tbl);
        fk_created := 'fk_' || tbl || '_created_by';
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = fk_created AND conrelid = ('public.' || tbl)::regclass
        ) AND NOT EXISTS (
            SELECT 1 FROM pg_constraint c
            JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
            WHERE c.conrelid = ('public.' || tbl)::regclass AND c.contype = 'f' AND a.attname = 'created_by'
        ) THEN
            EXECUTE format('ALTER TABLE public.%I ADD CONSTRAINT %I FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL;', tbl, fk_created);
        END IF;

        -- 3. updated_at
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT now();', tbl);

        -- 4. updated_by
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS updated_by BIGINT;', tbl);
        fk_updated := 'fk_' || tbl || '_updated_by';
        IF NOT EXISTS (
            SELECT 1 FROM pg_constraint WHERE conname = fk_updated AND conrelid = ('public.' || tbl)::regclass
        ) AND NOT EXISTS (
            SELECT 1 FROM pg_constraint c
            JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = ANY(c.conkey)
            WHERE c.conrelid = ('public.' || tbl)::regclass AND c.contype = 'f' AND a.attname = 'updated_by'
        ) THEN
            EXECUTE format('ALTER TABLE public.%I ADD CONSTRAINT %I FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL;', tbl, fk_updated);
        END IF;

        -- 5. status
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT ''active'';', tbl);

        -- 6. deleted_at
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE;', tbl);

        -- 7. is_deleted
        EXECUTE format('ALTER TABLE public.%I ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT false;', tbl);

        -- Trigger updated_at
        trg_name := 'trg_' || tbl || '_set_updated_at';
        IF NOT EXISTS (
            SELECT 1 FROM pg_trigger WHERE tgname = trg_name AND tgrelid = ('public.' || tbl)::regclass
        ) AND NOT EXISTS (
            SELECT 1 FROM pg_trigger WHERE tgrelid = ('public.' || tbl)::regclass AND tgname LIKE '%updated_at'
        ) THEN
            EXECUTE format('CREATE TRIGGER %I BEFORE UPDATE ON public.%I FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();', trg_name, tbl);
        END IF;

        -- Sync is_deleted with deleted_at
        EXECUTE format('UPDATE public.%I SET is_deleted = (deleted_at IS NOT NULL) WHERE is_deleted IS DISTINCT FROM (deleted_at IS NOT NULL);', tbl);
    END LOOP;

    -- Data sync for special columns
    UPDATE public.applications SET created_at = submitted_at WHERE created_at IS DISTINCT FROM submitted_at;
    UPDATE public.materials SET created_by = uploaded_by WHERE created_by IS NULL AND uploaded_by IS NOT NULL;
    UPDATE public.chat_participants SET created_by = added_by WHERE created_by IS NULL AND added_by IS NOT NULL;
    UPDATE public.chat_participants SET created_at = joined_at WHERE joined_at IS NOT NULL;
    UPDATE public.project_members SET created_at = joined_at WHERE joined_at IS NOT NULL;
    UPDATE public.task_assignees SET created_at = assigned_at WHERE assigned_at IS NOT NULL;
END $$;

COMMIT;
