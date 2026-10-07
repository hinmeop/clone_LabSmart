-- =====================================================================
-- SMARTLAB - FULL SCHEMA (CORE + CHAT) - ONE-FILE SCRIPT FOR DBEAVER
-- PostgreSQL 13+ (test tr�n 16.9)
-- =====================================================================
-- HU?NG D?N CH?Y TRONG DBEAVER
-- 1) T?o database tru?c (ch?y RI�NG, khi dang k?t n?i t?i DB "postgres"):
--      CREATE DATABASE smartlab
--        WITH TEMPLATE = template0
--        ENCODING = 'UTF8'
--        LOCALE_PROVIDER = libc
--        LOCALE = 'English_New Zealand.1252';   -- Windows; Linux d�ng 'en_US.UTF-8'
--    (Mu?n xo� s?ch: DROP DATABASE smartlab;  -- ph?i ng?t h?t connection tru?c)
-- 2) T?o/ch?n connection tr? t?i database "smartlab".
-- 3) M? file n�y trong SQL Editor -> Execute script (Alt+X), KH�NG d�ng Ctrl+Enter.
-- 4) Window > Preferences > Editors > set file encoding = UTF-8 (script c� ti?ng Vi?t).
-- 5) Script idempotent: ch?y l?i nhi?u l?n kh�ng l?i. LUU �: n?u b?ng d� t?n t?i v?i
--    d?nh nghia kh�c, "IF NOT EXISTS" s? B? QUA (kh�ng s?a) -> mu?n c�i s?ch th� d�ng
--    block RESET ? M?C 0 (dang comment).
-- =====================================================================

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA IF NOT EXISTS public;
COMMENT ON SCHEMA public IS 'standard public schema';

SET search_path = public;

BEGIN;

-- =====================================================================
-- M?C 0. (TU? CH?N) RESET S?CH - B? COMMENT N?U MU?N C�I L?I T? �?U
-- =====================================================================
/*
DROP VIEW IF EXISTS public.v_chat_messages CASCADE;
DROP VIEW IF EXISTS public.v_chat_user_conversations CASCADE;
DROP VIEW IF EXISTS public.v_chat_members CASCADE;

DROP TABLE IF EXISTS public.chat_message_mentions    CASCADE;
DROP TABLE IF EXISTS public.chat_message_reactions   CASCADE;
DROP TABLE IF EXISTS public.chat_message_attachments CASCADE;
DROP TABLE IF EXISTS public.chat_participants        CASCADE;
DROP TABLE IF EXISTS public.chat_messages            CASCADE;
DROP TABLE IF EXISTS public.chat_conversations       CASCADE;

DROP TABLE IF EXISTS public.report_exports           CASCADE;
DROP TABLE IF EXISTS public.notifications            CASCADE;
DROP TABLE IF EXISTS public.materials                CASCADE;
DROP TABLE IF EXISTS public.folders                  CASCADE;
DROP TABLE IF EXISTS public.interviews               CASCADE;
DROP TABLE IF EXISTS public.applications             CASCADE;
DROP TABLE IF EXISTS public.recruitment_form_fields  CASCADE;
DROP TABLE IF EXISTS public.recruitment_posts        CASCADE;
DROP TABLE IF EXISTS public.post_comments            CASCADE;
DROP TABLE IF EXISTS public.post_attachments         CASCADE;
DROP TABLE IF EXISTS public.posts                    CASCADE;
DROP TABLE IF EXISTS public.news                     CASCADE;
DROP TABLE IF EXISTS public.task_logworks            CASCADE;
DROP TABLE IF EXISTS public.task_logs                CASCADE;
DROP TABLE IF EXISTS public.task_comments            CASCADE;
DROP TABLE IF EXISTS public.task_assignees           CASCADE;
DROP TABLE IF EXISTS public.tasks                    CASCADE;
DROP TABLE IF EXISTS public.project_members          CASCADE;
DROP TABLE IF EXISTS public.projects                 CASCADE;
DROP TABLE IF EXISTS public.project_types            CASCADE;
DROP TABLE IF EXISTS public.domains                  CASCADE;
DROP TABLE IF EXISTS public.notification_settings    CASCADE;
DROP TABLE IF EXISTS public.system_settings          CASCADE;
DROP TABLE IF EXISTS public.auth_verification_codes  CASCADE;
DROP TABLE IF EXISTS public.users                    CASCADE;

DROP FUNCTION IF EXISTS public.trg_set_task_level()  CASCADE;
DROP FUNCTION IF EXISTS public.trg_set_updated_at()  CASCADE;
*/

-- =====================================================================
-- M?C 1. FUNCTIONS
-- =====================================================================

CREATE OR REPLACE FUNCTION public.trg_set_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION public.trg_set_task_level()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    parent_level SMALLINT;
BEGIN
    IF NEW.parent_task_id IS NULL THEN
        NEW.level := 1;
    ELSE
        SELECT t.level INTO parent_level
          FROM public.tasks t
         WHERE t.id = NEW.parent_task_id;

        IF parent_level IS NULL THEN
            RAISE EXCEPTION 'parent_task_id % kh�ng t?n t?i', NEW.parent_task_id;
        END IF;

        NEW.level := parent_level + 1;

        IF NEW.level > 3 THEN
            RAISE EXCEPTION 'Task ch? du?c ph�n c?p t?i da 3 c?p';
        END IF;
    END IF;
    RETURN NEW;
END;
$$;

SET default_tablespace = '';
SET default_table_access_method = heap;

-- =====================================================================
-- M?C 2. B?NG N?N: USER / AUTH / C?U H�NH
-- =====================================================================

-- 2.1 users
CREATE TABLE IF NOT EXISTS public.users (
    id              bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    org_email       character varying(255) NOT NULL,
    password_hash   character varying(255),
    full_name       character varying(255) NOT NULL,
    date_of_birth   date,
    department      character varying(255),
    note            character varying(1000),
    avatar_url      text,
    phone           character varying(20),
    role            character varying(20) NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at      timestamp without time zone,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_org_email_key UNIQUE (org_email),
    CONSTRAINT chk_users_dob CHECK (date_of_birth IS NULL OR date_of_birth <= CURRENT_DATE),
    CONSTRAINT chk_users_role CHECK (role IN ('it_admin','business_admin','manager','leader','member','guest')),
    CONSTRAINT chk_users_status CHECK (status IN ('active','inactive','locked')),

    CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 2.2 auth_verification_codes
CREATE TABLE IF NOT EXISTS public.auth_verification_codes (
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id       bigint NOT NULL,
    channel       character varying(10) NOT NULL,
    purpose       character varying(20) DEFAULT 'password_reset' NOT NULL,
    code_hash     character varying(255) NOT NULL,
    attempt_count integer DEFAULT 0 NOT NULL,
    expires_at    timestamp without time zone NOT NULL,
    used_at       timestamp without time zone,
    created_at    timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT auth_verification_codes_pkey PRIMARY KEY (id),
    CONSTRAINT auth_verification_codes_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users(id) ON DELETE CASCADE,
    CONSTRAINT chk_avc_channel CHECK (channel IN ('email','phone')),
    CONSTRAINT chk_avc_purpose CHECK (purpose = 'password_reset'),

    CONSTRAINT fk_auth_verification_codes_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_auth_verification_codes_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 2.3 system_settings
CREATE TABLE IF NOT EXISTS public.system_settings (
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    setting_key   character varying(100) NOT NULL,
    setting_value text,
    updated_by    bigint,
    updated_at    timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT system_settings_pkey PRIMARY KEY (id),
    CONSTRAINT system_settings_setting_key_key UNIQUE (setting_key),
    CONSTRAINT system_settings_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id),

    CONSTRAINT fk_system_settings_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 2.4 notification_settings
CREATE TABLE IF NOT EXISTS public.notification_settings (
    id                bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    notification_type character varying(100) NOT NULL,
    is_enabled        boolean DEFAULT true NOT NULL,
    updated_by        bigint,
    updated_at        timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT notification_settings_pkey PRIMARY KEY (id),
    CONSTRAINT notification_settings_notification_type_key UNIQUE (notification_type),
    CONSTRAINT notification_settings_updated_by_fkey FOREIGN KEY (updated_by) REFERENCES public.users(id),

    CONSTRAINT fk_notification_settings_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 3. DANH M?C & PROJECT
-- =====================================================================

-- 3.1 domains
CREATE TABLE IF NOT EXISTS public.domains (
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        character varying(255) NOT NULL,
    description text,
    status      character varying(20) DEFAULT 'active' NOT NULL,
    created_at  timestamp without time zone DEFAULT now() NOT NULL,
    updated_at  timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT domains_pkey PRIMARY KEY (id),
    CONSTRAINT domains_name_key UNIQUE (name),
    CONSTRAINT chk_domains_status CHECK (status IN ('active','inactive')),

    CONSTRAINT fk_domains_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_domains_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 3.2 project_types
CREATE TABLE IF NOT EXISTS public.project_types (
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    name        character varying(255) NOT NULL,
    description text,
    status      character varying(20) DEFAULT 'active' NOT NULL,
    created_at  timestamp without time zone DEFAULT now() NOT NULL,
    updated_at  timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT project_types_pkey PRIMARY KEY (id),
    CONSTRAINT project_types_name_key UNIQUE (name),
    CONSTRAINT chk_ptype_status CHECK (status IN ('active','inactive')),

    CONSTRAINT fk_project_types_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_project_types_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 3.3 projects
CREATE TABLE IF NOT EXISTS public.projects (
    id              bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    code            character varying(50) NOT NULL,
    name            character varying(255) NOT NULL,
    description     text,
    domain_id       bigint NOT NULL,
    project_type_id bigint NOT NULL,
    status          character varying(20) DEFAULT 'pending' NOT NULL,
    progress        numeric(5,2) DEFAULT 0 NOT NULL,
    start_date      date,
    end_date        date,
    cancel_reason   text,
    is_featured     boolean DEFAULT false NOT NULL,
    created_by      bigint NOT NULL,
    owner_id        bigint NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at      timestamp without time zone,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT projects_pkey PRIMARY KEY (id),
    CONSTRAINT projects_code_key UNIQUE (code),
    CONSTRAINT projects_domain_id_fkey       FOREIGN KEY (domain_id)       REFERENCES public.domains(id),
    CONSTRAINT projects_project_type_id_fkey FOREIGN KEY (project_type_id) REFERENCES public.project_types(id),
    CONSTRAINT projects_created_by_fkey      FOREIGN KEY (created_by)      REFERENCES public.users(id),
    CONSTRAINT projects_owner_id_fkey        FOREIGN KEY (owner_id)        REFERENCES public.users(id),
    CONSTRAINT chk_projects_status CHECK (status IN ('pending','processing','done','cancel')),
    CONSTRAINT chk_projects_progress CHECK (progress >= 0 AND progress <= 100),
    CONSTRAINT chk_projects_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_projects_cancel_reason CHECK (status <> 'cancel' OR cancel_reason IS NOT NULL),

    CONSTRAINT fk_projects_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 3.4 project_members
CREATE TABLE IF NOT EXISTS public.project_members (
    id                    bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    project_id            bigint NOT NULL,
    user_id               bigint NOT NULL,
    project_role          character varying(20) NOT NULL,
    team_leader_member_id bigint,
    joined_at             timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT project_members_pkey PRIMARY KEY (id),
    CONSTRAINT uq_project_member UNIQUE (project_id, user_id),
    CONSTRAINT project_members_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES public.projects(id) ON DELETE CASCADE,
    CONSTRAINT project_members_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT project_members_team_leader_member_id_fkey FOREIGN KEY (team_leader_member_id)
        REFERENCES public.project_members(id),
    CONSTRAINT chk_pm_role CHECK (project_role IN ('owner','coordinator','leader','member')),

    CONSTRAINT fk_project_members_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_project_members_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 4. TASK
-- =====================================================================

-- 4.1 tasks
CREATE TABLE IF NOT EXISTS public.tasks (
    id               bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    code             character varying(50) NOT NULL,
    project_id       bigint NOT NULL,
    parent_task_id   bigint,
    level            smallint DEFAULT 1 NOT NULL,
    name             character varying(255) NOT NULL,
    description      text,
    owner_id         bigint NOT NULL,
    start_date       date,
    end_date         date,
    status           character varying(20) DEFAULT 'pending' NOT NULL,
    progress         numeric(5,2) DEFAULT 0 NOT NULL,
    result_file_url  text,
    submit_note      text,
    reviewer_comment text,
    cancel_reason    text,
    created_at       timestamp without time zone DEFAULT now() NOT NULL,
    updated_at       timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at       timestamp without time zone,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT tasks_pkey PRIMARY KEY (id),
    CONSTRAINT tasks_code_key UNIQUE (code),
    CONSTRAINT tasks_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES public.projects(id) ON DELETE CASCADE,
    CONSTRAINT tasks_parent_task_id_fkey FOREIGN KEY (parent_task_id) REFERENCES public.tasks(id),
    CONSTRAINT tasks_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES public.users(id),
    CONSTRAINT chk_tasks_status CHECK (status IN ('pending','processing','pending_review','done','rejected','cancel')),
    CONSTRAINT chk_tasks_level CHECK (level >= 1 AND level <= 3),
    CONSTRAINT chk_tasks_progress CHECK (progress >= 0 AND progress <= 100),
    CONSTRAINT chk_tasks_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date),
    CONSTRAINT chk_tasks_cancel_reason CHECK (status <> 'cancel' OR cancel_reason IS NOT NULL),
    CONSTRAINT chk_tasks_reject_comment CHECK (status <> 'rejected' OR reviewer_comment IS NOT NULL),

    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_tasks_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 4.2 task_assignees
CREATE TABLE IF NOT EXISTS public.task_assignees (
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    task_id     bigint NOT NULL,
    user_id     bigint NOT NULL,
    assigned_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT task_assignees_pkey PRIMARY KEY (id),
    CONSTRAINT uq_task_assignee UNIQUE (task_id, user_id),
    CONSTRAINT task_assignees_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT task_assignees_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),

    CONSTRAINT fk_task_assignees_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_assignees_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 4.3 task_comments
CREATE TABLE IF NOT EXISTS public.task_comments (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    task_id    bigint NOT NULL,
    user_id    bigint NOT NULL,
    content    text NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at timestamp without time zone,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT task_comments_pkey PRIMARY KEY (id),
    CONSTRAINT task_comments_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT task_comments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT chk_task_comment_not_empty CHECK (length(btrim(content)) > 0),

    CONSTRAINT fk_task_comments_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_comments_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 4.4 task_logs (audit)
CREATE TABLE IF NOT EXISTS public.task_logs (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    task_id    bigint NOT NULL,
    user_id    bigint NOT NULL,
    action     character varying(50) NOT NULL,
    field_name character varying(100),
    old_value  text,
    new_value  text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT task_logs_pkey PRIMARY KEY (id),
    CONSTRAINT task_logs_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT task_logs_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),

    CONSTRAINT fk_task_logs_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_logs_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 4.5 task_logworks (timesheet)
CREATE TABLE IF NOT EXISTS public.task_logworks (
    id               bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    task_id          bigint NOT NULL,
    user_id          bigint NOT NULL,
    work_date        date NOT NULL,
    started_at       timestamp without time zone,
    ended_at         timestamp without time zone,
    duration_minutes integer NOT NULL,
    description      text NOT NULL,
    created_at       timestamp without time zone DEFAULT now() NOT NULL,
    updated_at       timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at       timestamp without time zone,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT task_logworks_pkey PRIMARY KEY (id),
    CONSTRAINT task_logworks_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.tasks(id) ON DELETE CASCADE,
    CONSTRAINT task_logworks_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT chk_logwork_description CHECK (length(btrim(description)) > 0),
    CONSTRAINT chk_logwork_duration CHECK (duration_minutes >= 1 AND duration_minutes <= 1440),
    CONSTRAINT chk_logwork_work_date CHECK (work_date <= CURRENT_DATE),
    CONSTRAINT chk_logwork_time_pair CHECK (
        (started_at IS NULL AND ended_at IS NULL)
        OR (started_at IS NOT NULL AND ended_at IS NOT NULL AND ended_at > started_at)
    ),
    CONSTRAINT chk_logwork_time_duration CHECK (
        started_at IS NULL
        OR duration_minutes = floor(EXTRACT(epoch FROM (ended_at - started_at)) / 60)::integer
    ),

    CONSTRAINT fk_task_logworks_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_logworks_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 5. NEWS / POST
-- =====================================================================

-- 5.1 news
CREATE TABLE IF NOT EXISTS public.news (
    id              bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    title           character varying(255) NOT NULL,
    content         text NOT NULL,
    cover_image_url text,
    status          character varying(20) DEFAULT 'draft' NOT NULL,
    is_pinned       boolean DEFAULT false NOT NULL,
    publish_at      timestamp without time zone,
    created_by      bigint NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at      timestamp without time zone,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT news_pkey PRIMARY KEY (id),
    CONSTRAINT news_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
    CONSTRAINT chk_news_status CHECK (status IN ('draft','scheduled','published','hidden')),

    CONSTRAINT fk_news_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 5.2 posts
CREATE TABLE IF NOT EXISTS public.posts (
    id            bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    title         character varying(255) NOT NULL,
    content       text NOT NULL,
    scope         character varying(10) NOT NULL,
    project_id    bigint,
    allow_comment boolean DEFAULT true NOT NULL,
    is_pinned     boolean DEFAULT false NOT NULL,
    status        character varying(20) DEFAULT 'draft' NOT NULL,
    created_by    bigint NOT NULL,
    reviewed_by   bigint,
    reject_reason text,
    created_at    timestamp without time zone DEFAULT now() NOT NULL,
    updated_at    timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at    timestamp without time zone,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT posts_pkey PRIMARY KEY (id),
    CONSTRAINT posts_project_id_fkey  FOREIGN KEY (project_id)  REFERENCES public.projects(id),
    CONSTRAINT posts_created_by_fkey  FOREIGN KEY (created_by)  REFERENCES public.users(id),
    CONSTRAINT posts_reviewed_by_fkey FOREIGN KEY (reviewed_by) REFERENCES public.users(id),
    CONSTRAINT chk_posts_scope CHECK (scope IN ('lab','project')),
    CONSTRAINT chk_posts_status CHECK (status IN ('draft','pending_review','published','hidden')),
    CONSTRAINT chk_posts_scope_project CHECK (
        (scope = 'project' AND project_id IS NOT NULL)
        OR (scope = 'lab' AND project_id IS NULL)
    ),

    CONSTRAINT fk_posts_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 5.3 post_attachments
CREATE TABLE IF NOT EXISTS public.post_attachments (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    post_id    bigint NOT NULL,
    file_url   text NOT NULL,
    file_name  character varying(255),
    file_type  character varying(50),
    file_size  bigint,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT post_attachments_pkey PRIMARY KEY (id),
    CONSTRAINT post_attachments_post_id_fkey FOREIGN KEY (post_id)
        REFERENCES public.posts(id) ON DELETE CASCADE,

    CONSTRAINT fk_post_attachments_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_post_attachments_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 5.4 post_comments
CREATE TABLE IF NOT EXISTS public.post_comments (
    id                bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    post_id           bigint NOT NULL,
    user_id           bigint NOT NULL,
    parent_comment_id bigint,
    content           text NOT NULL,
    created_at        timestamp without time zone DEFAULT now() NOT NULL,
    updated_at        timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at        timestamp without time zone,
    deleted_by        bigint,
    delete_reason     text,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT post_comments_pkey PRIMARY KEY (id),
    CONSTRAINT post_comments_post_id_fkey FOREIGN KEY (post_id)
        REFERENCES public.posts(id) ON DELETE CASCADE,
    CONSTRAINT post_comments_user_id_fkey           FOREIGN KEY (user_id)           REFERENCES public.users(id),
    CONSTRAINT post_comments_deleted_by_fkey        FOREIGN KEY (deleted_by)        REFERENCES public.users(id),
    CONSTRAINT post_comments_parent_comment_id_fkey FOREIGN KEY (parent_comment_id) REFERENCES public.post_comments(id),
    CONSTRAINT chk_post_comment_not_empty CHECK (length(btrim(content)) > 0),

    CONSTRAINT fk_post_comments_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_post_comments_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 6. TUY?N D?NG
-- =====================================================================

-- 6.1 recruitment_posts
CREATE TABLE IF NOT EXISTS public.recruitment_posts (
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    title       character varying(255) NOT NULL,
    description text,
    domain_id   bigint,
    project_id  bigint,
    quantity    integer DEFAULT 1 NOT NULL,
    deadline    date NOT NULL,
    status      character varying(20) DEFAULT 'draft' NOT NULL,
    created_by  bigint NOT NULL,
    created_at  timestamp without time zone DEFAULT now() NOT NULL,
    updated_at  timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at  timestamp without time zone,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT recruitment_posts_pkey PRIMARY KEY (id),
    CONSTRAINT recruitment_posts_domain_id_fkey  FOREIGN KEY (domain_id)  REFERENCES public.domains(id),
    CONSTRAINT recruitment_posts_project_id_fkey FOREIGN KEY (project_id) REFERENCES public.projects(id),
    CONSTRAINT recruitment_posts_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
    CONSTRAINT chk_recruit_quantity CHECK (quantity > 0),
    CONSTRAINT chk_recruit_status CHECK (status IN ('draft','open','closed')),

    CONSTRAINT fk_recruitment_posts_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 6.2 recruitment_form_fields
CREATE TABLE IF NOT EXISTS public.recruitment_form_fields (
    id                  bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    recruitment_post_id bigint NOT NULL,
    field_name          character varying(100) NOT NULL,
    field_label         character varying(255) NOT NULL,
    field_type          character varying(20) DEFAULT 'text' NOT NULL,
    is_required         boolean DEFAULT true NOT NULL,
    order_index         integer DEFAULT 0 NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT recruitment_form_fields_pkey PRIMARY KEY (id),
    CONSTRAINT uq_rff_field UNIQUE (recruitment_post_id, field_name),
    CONSTRAINT recruitment_form_fields_recruitment_post_id_fkey FOREIGN KEY (recruitment_post_id)
        REFERENCES public.recruitment_posts(id) ON DELETE CASCADE,
    CONSTRAINT chk_rff_type CHECK (field_type IN ('text','number','email','phone','file','textarea')),

    CONSTRAINT fk_recruitment_form_fields_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_recruitment_form_fields_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 6.3 applications
CREATE TABLE IF NOT EXISTS public.applications (
    id                  bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    recruitment_post_id bigint NOT NULL,
    full_name           character varying(255) NOT NULL,
    student_id          character varying(50),
    email               character varying(255) NOT NULL,
    phone               character varying(20),
    cv_url              text,
    skills              text,
    answers             jsonb,
    status              character varying(20) DEFAULT 'pending' NOT NULL,
    evaluation_note     text,
    created_user_id     bigint,
    submitted_at        timestamp without time zone DEFAULT now() NOT NULL,
    updated_at          timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at          timestamp without time zone,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT applications_pkey PRIMARY KEY (id),
    CONSTRAINT applications_recruitment_post_id_fkey FOREIGN KEY (recruitment_post_id)
        REFERENCES public.recruitment_posts(id),
    CONSTRAINT applications_created_user_id_fkey FOREIGN KEY (created_user_id) REFERENCES public.users(id),
    CONSTRAINT chk_app_status CHECK (status IN ('pending','interview','passed','failed','withdrawn')),

    CONSTRAINT fk_applications_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_applications_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 6.4 interviews
CREATE TABLE IF NOT EXISTS public.interviews (
    id               bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    application_id   bigint NOT NULL,
    scheduled_time   timestamp without time zone NOT NULL,
    format           character varying(10) NOT NULL,
    location_or_link text NOT NULL,
    interviewer_id   bigint NOT NULL,
    note             text,
    status           character varying(20) DEFAULT 'scheduled' NOT NULL,
    cancel_reason    text,
    created_at       timestamp without time zone DEFAULT now() NOT NULL,
    updated_at       timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT interviews_pkey PRIMARY KEY (id),
    CONSTRAINT interviews_application_id_fkey FOREIGN KEY (application_id)
        REFERENCES public.applications(id) ON DELETE CASCADE,
    CONSTRAINT interviews_interviewer_id_fkey FOREIGN KEY (interviewer_id) REFERENCES public.users(id),
    CONSTRAINT chk_iv_format CHECK (format IN ('offline','online')),
    CONSTRAINT chk_iv_status CHECK (status IN ('scheduled','completed','cancelled')),
    CONSTRAINT chk_iv_cancel_reason CHECK (status <> 'cancelled' OR cancel_reason IS NOT NULL),

    CONSTRAINT fk_interviews_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_interviews_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 7. T�I LI?U / TH�NG B�O / B�O C�O
-- =====================================================================

-- 7.1 folders
CREATE TABLE IF NOT EXISTS public.folders (
    id               bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    name             character varying(255) NOT NULL,
    parent_folder_id bigint,
    project_id       bigint,
    created_by       bigint NOT NULL,
    created_at       timestamp without time zone DEFAULT now() NOT NULL,
    updated_at       timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at       timestamp without time zone,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT folders_pkey PRIMARY KEY (id),
    CONSTRAINT folders_parent_folder_id_fkey FOREIGN KEY (parent_folder_id) REFERENCES public.folders(id),
    CONSTRAINT folders_project_id_fkey       FOREIGN KEY (project_id)       REFERENCES public.projects(id),
    CONSTRAINT folders_created_by_fkey       FOREIGN KEY (created_by)       REFERENCES public.users(id),

    CONSTRAINT fk_folders_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 7.2 materials
CREATE TABLE IF NOT EXISTS public.materials (
    id          bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    folder_id   bigint NOT NULL,
    name        character varying(255) NOT NULL,
    file_url    text NOT NULL,
    file_type   character varying(50),
    file_size   bigint,
    uploaded_by bigint NOT NULL,
    created_at  timestamp without time zone DEFAULT now() NOT NULL,
    updated_at  timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at  timestamp without time zone,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT materials_pkey PRIMARY KEY (id),
    CONSTRAINT materials_folder_id_fkey   FOREIGN KEY (folder_id)   REFERENCES public.folders(id),
    CONSTRAINT materials_uploaded_by_fkey FOREIGN KEY (uploaded_by) REFERENCES public.users(id),

    CONSTRAINT fk_materials_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_materials_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 7.3 notifications
CREATE TABLE IF NOT EXISTS public.notifications (
    id                  bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id             bigint NOT NULL,
    title               character varying(255) NOT NULL,
    content             text,
    related_entity_type character varying(50),
    related_entity_id   bigint,
    is_read             boolean DEFAULT false NOT NULL,
    created_at          timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT notifications_pkey PRIMARY KEY (id),
    CONSTRAINT notifications_user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public.users(id) ON DELETE CASCADE,

    CONSTRAINT fk_notifications_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_notifications_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 7.4 report_exports
CREATE TABLE IF NOT EXISTS public.report_exports (
    id           bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    requested_by bigint NOT NULL,
    project_id   bigint,
    semester     character varying(10),
    year         integer NOT NULL,
    export_type  character varying(10) DEFAULT 'manual' NOT NULL,
    ai_prompt    text,
    file_url     text,
    status       character varying(20) DEFAULT 'processing' NOT NULL,
    created_at   timestamp without time zone DEFAULT now() NOT NULL,
    completed_at timestamp without time zone,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT report_exports_pkey PRIMARY KEY (id),
    CONSTRAINT report_exports_requested_by_fkey FOREIGN KEY (requested_by) REFERENCES public.users(id),
    CONSTRAINT report_exports_project_id_fkey   FOREIGN KEY (project_id)   REFERENCES public.projects(id),
    CONSTRAINT chk_export_semester CHECK (semester IN ('HK1','HK2','HK3')),
    CONSTRAINT chk_export_type CHECK (export_type IN ('manual','ai')),
    CONSTRAINT chk_export_status CHECK (status IN ('processing','completed','failed','cancelled')),
    CONSTRAINT chk_export_ai_prompt CHECK (export_type <> 'ai' OR ai_prompt IS NOT NULL),

    CONSTRAINT fk_report_exports_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_report_exports_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 8. PH�N H? CHAT (1-1 + GROUP)
-- =====================================================================

-- 8.1 chat_conversations
CREATE TABLE IF NOT EXISTS public.chat_conversations (
    id                bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    conversation_type character varying(10) NOT NULL,
    name              character varying(255),
    description       text,
    avatar_url        text,
    direct_user_low   bigint,
    direct_user_high  bigint,
    project_id        bigint,
    last_message_id   bigint,
    last_message_at   timestamp without time zone,
    created_by        bigint NOT NULL,
    created_at        timestamp without time zone DEFAULT now() NOT NULL,
    updated_at        timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at        timestamp without time zone,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_conversations_pkey PRIMARY KEY (id),
    CONSTRAINT chat_conversations_created_by_fkey FOREIGN KEY (created_by) REFERENCES public.users(id),
    CONSTRAINT chat_conversations_project_id_fkey FOREIGN KEY (project_id)
        REFERENCES public.projects(id) ON DELETE SET NULL,
    CONSTRAINT chat_conversations_direct_low_fkey  FOREIGN KEY (direct_user_low)  REFERENCES public.users(id),
    CONSTRAINT chat_conversations_direct_high_fkey FOREIGN KEY (direct_user_high) REFERENCES public.users(id),
    CONSTRAINT chk_chat_conv_type CHECK (conversation_type IN ('direct','group')),
    CONSTRAINT chk_chat_conv_direct CHECK (
        conversation_type <> 'direct'
        OR (direct_user_low IS NOT NULL AND direct_user_high IS NOT NULL
            AND direct_user_low < direct_user_high
            AND name IS NULL AND project_id IS NULL)
    ),
    CONSTRAINT chk_chat_conv_group CHECK (
        conversation_type <> 'group'
        OR (direct_user_low IS NULL AND direct_user_high IS NULL
            AND name IS NOT NULL AND length(btrim(name)) > 0)
    ),

    CONSTRAINT fk_chat_conversations_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 8.2 chat_messages
CREATE TABLE IF NOT EXISTS public.chat_messages (
    id                  bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    conversation_id     bigint NOT NULL,
    sender_id           bigint,
    message_type        character varying(10) DEFAULT 'text' NOT NULL,
    content             text,
    reply_to_message_id bigint,
    client_message_id   uuid,
    metadata            jsonb DEFAULT '{}'::jsonb NOT NULL,
    edited_at           timestamp without time zone,
    created_at          timestamp without time zone DEFAULT now() NOT NULL,
    updated_at          timestamp without time zone DEFAULT now() NOT NULL,
    deleted_at          timestamp without time zone,
    deleted_by          bigint,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_by      bigint,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_messages_pkey PRIMARY KEY (id),
    CONSTRAINT uq_chat_msg_id_conv UNIQUE (id, conversation_id),
    CONSTRAINT chat_messages_conversation_id_fkey FOREIGN KEY (conversation_id)
        REFERENCES public.chat_conversations(id) ON DELETE CASCADE,
    CONSTRAINT chat_messages_sender_id_fkey  FOREIGN KEY (sender_id)  REFERENCES public.users(id),
    CONSTRAINT chat_messages_deleted_by_fkey FOREIGN KEY (deleted_by) REFERENCES public.users(id),
    -- Reply ch? tr? t?i tin trong C�NG cu?c tr� chuy?n
    CONSTRAINT chat_messages_reply_fkey FOREIGN KEY (reply_to_message_id, conversation_id)
        REFERENCES public.chat_messages(id, conversation_id),
    CONSTRAINT chk_chat_msg_type CHECK (message_type IN ('text','image','file','system')),
    CONSTRAINT chk_chat_msg_sender CHECK (message_type = 'system' OR sender_id IS NOT NULL),
    CONSTRAINT chk_chat_msg_text CHECK (message_type <> 'text' OR (content IS NOT NULL AND length(btrim(content)) > 0)),
    CONSTRAINT chk_chat_msg_system CHECK (message_type <> 'system' OR content IS NOT NULL),
    CONSTRAINT chk_chat_msg_len CHECK (content IS NULL OR length(content) <= 10000),
    CONSTRAINT chk_chat_msg_deleted CHECK (deleted_by IS NULL OR deleted_at IS NOT NULL),

    CONSTRAINT fk_chat_messages_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_chat_messages_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 8.3 chat_participants
CREATE TABLE IF NOT EXISTS public.chat_participants (
    id                   bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    conversation_id      bigint NOT NULL,
    user_id              bigint NOT NULL,
    role                 character varying(10) DEFAULT 'member' NOT NULL,
    added_by             bigint,
    joined_at            timestamp without time zone DEFAULT now() NOT NULL,
    left_at              timestamp without time zone,
    last_read_message_id bigint,
    last_read_at         timestamp without time zone,
    is_muted             boolean DEFAULT false NOT NULL,
    is_pinned            boolean DEFAULT false NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_at      timestamp without time zone DEFAULT now() NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_participants_pkey PRIMARY KEY (id),
    CONSTRAINT uq_chat_participant UNIQUE (conversation_id, user_id),
    CONSTRAINT chat_participants_conversation_id_fkey FOREIGN KEY (conversation_id)
        REFERENCES public.chat_conversations(id) ON DELETE CASCADE,
    CONSTRAINT chat_participants_user_id_fkey  FOREIGN KEY (user_id)  REFERENCES public.users(id),
    CONSTRAINT chat_participants_added_by_fkey FOREIGN KEY (added_by) REFERENCES public.users(id),
    CONSTRAINT chat_participants_last_read_fkey FOREIGN KEY (last_read_message_id, conversation_id)
        REFERENCES public.chat_messages(id, conversation_id),
    CONSTRAINT chk_chat_part_role CHECK (role IN ('owner','admin','member')),

    CONSTRAINT fk_chat_participants_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_chat_participants_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 8.4 FK v�ng: last_message ph?i thu?c d�ng conversation
ALTER TABLE public.chat_conversations
    DROP CONSTRAINT IF EXISTS chat_conversations_last_message_fkey;
ALTER TABLE public.chat_conversations
    ADD CONSTRAINT chat_conversations_last_message_fkey
    FOREIGN KEY (last_message_id, id) REFERENCES public.chat_messages(id, conversation_id);

-- 8.5 chat_message_attachments
CREATE TABLE IF NOT EXISTS public.chat_message_attachments (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    message_id bigint NOT NULL,
    file_url   text NOT NULL,
    file_name  character varying(255),
    file_type  character varying(150),
    file_size  bigint,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_message_attachments_pkey PRIMARY KEY (id),
    CONSTRAINT chat_message_attachments_message_id_fkey FOREIGN KEY (message_id)
        REFERENCES public.chat_messages(id) ON DELETE CASCADE,
    CONSTRAINT chk_chat_att_size CHECK (file_size IS NULL OR file_size >= 0),
    CONSTRAINT chk_chat_att_url CHECK (length(btrim(file_url)) > 0),

    CONSTRAINT fk_chat_message_attachments_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_chat_message_attachments_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 8.6 chat_message_reactions
CREATE TABLE IF NOT EXISTS public.chat_message_reactions (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    message_id bigint NOT NULL,
    user_id    bigint NOT NULL,
    emoji      character varying(32) NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_message_reactions_pkey PRIMARY KEY (id),
    CONSTRAINT uq_chat_reaction UNIQUE (message_id, user_id, emoji),
    CONSTRAINT chat_message_reactions_message_id_fkey FOREIGN KEY (message_id)
        REFERENCES public.chat_messages(id) ON DELETE CASCADE,
    CONSTRAINT chat_message_reactions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
    CONSTRAINT chk_chat_reaction_emoji CHECK (length(btrim(emoji)) > 0),

    CONSTRAINT fk_chat_message_reactions_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_chat_message_reactions_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- 8.7 chat_message_mentions
CREATE TABLE IF NOT EXISTS public.chat_message_mentions (
    id         bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    message_id bigint NOT NULL,
    user_id    bigint NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    status          character varying(20) DEFAULT 'active' NOT NULL,
    created_by      bigint,
    updated_at      timestamp without time zone DEFAULT now() NOT NULL,
    updated_by      bigint,
    deleted_at      timestamp without time zone,
    is_deleted      boolean DEFAULT false NOT NULL,
    CONSTRAINT chat_message_mentions_pkey PRIMARY KEY (id),
    CONSTRAINT uq_chat_mention UNIQUE (message_id, user_id),
    CONSTRAINT chat_message_mentions_message_id_fkey FOREIGN KEY (message_id)
        REFERENCES public.chat_messages(id) ON DELETE CASCADE,
    CONSTRAINT chat_message_mentions_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),

    CONSTRAINT fk_chat_message_mentions_created_by FOREIGN KEY (created_by) REFERENCES public.users(id) ON DELETE SET NULL,
    CONSTRAINT fk_chat_message_mentions_updated_by FOREIGN KEY (updated_by) REFERENCES public.users(id) ON DELETE SET NULL
);

-- =====================================================================
-- M?C 9. INDEX
-- =====================================================================

-- users
CREATE INDEX IF NOT EXISTS idx_users_role   ON public.users USING btree (role);
CREATE INDEX IF NOT EXISTS idx_users_status ON public.users USING btree (status);

-- auth
CREATE INDEX IF NOT EXISTS idx_avc_user_id ON public.auth_verification_codes USING btree (user_id);

-- projects
CREATE INDEX IF NOT EXISTS idx_projects_domain ON public.projects USING btree (domain_id);
CREATE INDEX IF NOT EXISTS idx_projects_type   ON public.projects USING btree (project_type_id);
CREATE INDEX IF NOT EXISTS idx_projects_owner  ON public.projects USING btree (owner_id);
CREATE INDEX IF NOT EXISTS idx_projects_status ON public.projects USING btree (status);
CREATE UNIQUE INDEX IF NOT EXISTS uq_projects_name_per_domain
    ON public.projects USING btree (domain_id, name) WHERE deleted_at IS NULL;

-- project_members
CREATE INDEX IF NOT EXISTS idx_pm_project ON public.project_members USING btree (project_id);
CREATE INDEX IF NOT EXISTS idx_pm_user    ON public.project_members USING btree (user_id);
CREATE INDEX IF NOT EXISTS idx_pm_leader  ON public.project_members USING btree (team_leader_member_id);

-- tasks
CREATE INDEX IF NOT EXISTS idx_tasks_project ON public.tasks USING btree (project_id);
CREATE INDEX IF NOT EXISTS idx_tasks_parent  ON public.tasks USING btree (parent_task_id);
CREATE INDEX IF NOT EXISTS idx_tasks_owner   ON public.tasks USING btree (owner_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status  ON public.tasks USING btree (status);
CREATE UNIQUE INDEX IF NOT EXISTS uq_tasks_name_per_parent
    ON public.tasks USING btree (project_id, COALESCE(parent_task_id, 0::bigint), name)
    WHERE deleted_at IS NULL;

CREATE INDEX IF NOT EXISTS idx_task_assignees_task ON public.task_assignees USING btree (task_id);
CREATE INDEX IF NOT EXISTS idx_task_assignees_user ON public.task_assignees USING btree (user_id);
CREATE INDEX IF NOT EXISTS idx_task_comments_task  ON public.task_comments  USING btree (task_id);
CREATE INDEX IF NOT EXISTS idx_task_logs_task      ON public.task_logs      USING btree (task_id);
CREATE INDEX IF NOT EXISTS idx_task_logworks_task_date
    ON public.task_logworks USING btree (task_id, work_date DESC) WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_task_logworks_user_date
    ON public.task_logworks USING btree (user_id, work_date DESC) WHERE deleted_at IS NULL;

-- news / posts
CREATE INDEX IF NOT EXISTS idx_news_status          ON public.news             USING btree (status);
CREATE INDEX IF NOT EXISTS idx_posts_project        ON public.posts            USING btree (project_id);
CREATE INDEX IF NOT EXISTS idx_posts_status         ON public.posts            USING btree (status);
CREATE INDEX IF NOT EXISTS idx_post_attachments_post ON public.post_attachments USING btree (post_id);
CREATE INDEX IF NOT EXISTS idx_post_comments_post   ON public.post_comments    USING btree (post_id);

-- tuy?n d?ng
CREATE INDEX IF NOT EXISTS idx_recruit_domain  ON public.recruitment_posts USING btree (domain_id);
CREATE INDEX IF NOT EXISTS idx_recruit_project ON public.recruitment_posts USING btree (project_id);
CREATE INDEX IF NOT EXISTS idx_recruit_status  ON public.recruitment_posts USING btree (status);
CREATE INDEX IF NOT EXISTS idx_rff_post        ON public.recruitment_form_fields USING btree (recruitment_post_id);
CREATE INDEX IF NOT EXISTS idx_app_recruit     ON public.applications USING btree (recruitment_post_id);
CREATE INDEX IF NOT EXISTS idx_app_status      ON public.applications USING btree (status);
CREATE INDEX IF NOT EXISTS idx_iv_application  ON public.interviews   USING btree (application_id);
CREATE INDEX IF NOT EXISTS idx_iv_interviewer  ON public.interviews   USING btree (interviewer_id);

-- t�i li?u
CREATE INDEX IF NOT EXISTS idx_folders_parent     ON public.folders   USING btree (parent_folder_id);
CREATE INDEX IF NOT EXISTS idx_folders_project    ON public.folders   USING btree (project_id);
CREATE UNIQUE INDEX IF NOT EXISTS uq_folders_name_per_parent
    ON public.folders USING btree (COALESCE(parent_folder_id, 0::bigint), name)
    WHERE deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_materials_folder   ON public.materials USING btree (folder_id);
CREATE INDEX IF NOT EXISTS idx_materials_uploader ON public.materials USING btree (uploaded_by);

-- th�ng b�o / b�o c�o
CREATE INDEX IF NOT EXISTS idx_notifications_user ON public.notifications USING btree (user_id, is_read);
CREATE INDEX IF NOT EXISTS idx_exports_requester  ON public.report_exports USING btree (requested_by);
CREATE INDEX IF NOT EXISTS idx_exports_project    ON public.report_exports USING btree (project_id);

-- chat: conversations
CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_conv_direct_pair
    ON public.chat_conversations USING btree (direct_user_low, direct_user_high)
    WHERE conversation_type = 'direct';
CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_conv_project
    ON public.chat_conversations USING btree (project_id)
    WHERE project_id IS NOT NULL AND conversation_type = 'group' AND deleted_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_chat_conv_last_message_at
    ON public.chat_conversations USING btree (last_message_at DESC NULLS LAST);
CREATE INDEX IF NOT EXISTS idx_chat_conv_direct_high
    ON public.chat_conversations USING btree (direct_user_high)
    WHERE conversation_type = 'direct';

-- chat: participants
CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_part_single_owner
    ON public.chat_participants USING btree (conversation_id)
    WHERE role = 'owner' AND left_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_chat_part_user_active
    ON public.chat_participants USING btree (user_id, conversation_id) WHERE left_at IS NULL;
CREATE INDEX IF NOT EXISTS idx_chat_part_conv_active
    ON public.chat_participants USING btree (conversation_id) WHERE left_at IS NULL;

-- chat: messages
CREATE INDEX IF NOT EXISTS idx_chat_msg_conv_id ON public.chat_messages USING btree (conversation_id, id DESC);
CREATE INDEX IF NOT EXISTS idx_chat_msg_sender  ON public.chat_messages USING btree (sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_msg_reply
    ON public.chat_messages USING btree (reply_to_message_id) WHERE reply_to_message_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_chat_msg_client_id
    ON public.chat_messages USING btree (sender_id, client_message_id) WHERE client_message_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_chat_msg_fts
    ON public.chat_messages USING gin (to_tsvector('simple'::regconfig, COALESCE(content, '')))
    WHERE deleted_at IS NULL;

-- chat: ph? tr?
CREATE INDEX IF NOT EXISTS idx_chat_att_message   ON public.chat_message_attachments USING btree (message_id);
CREATE INDEX IF NOT EXISTS idx_chat_reaction_message ON public.chat_message_reactions USING btree (message_id);
CREATE INDEX IF NOT EXISTS idx_chat_mention_user  ON public.chat_message_mentions USING btree (user_id);

-- =====================================================================
-- M?C 10. TRIGGER
-- =====================================================================

DROP TRIGGER IF EXISTS trg_users_updated_at ON public.users;
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON public.users
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_settings_updated_at ON public.system_settings;
CREATE TRIGGER trg_settings_updated_at BEFORE UPDATE ON public.system_settings
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_notif_settings_updated_at ON public.notification_settings;
CREATE TRIGGER trg_notif_settings_updated_at BEFORE UPDATE ON public.notification_settings
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_domains_updated_at ON public.domains;
CREATE TRIGGER trg_domains_updated_at BEFORE UPDATE ON public.domains
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_ptypes_updated_at ON public.project_types;
CREATE TRIGGER trg_ptypes_updated_at BEFORE UPDATE ON public.project_types
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_projects_updated_at ON public.projects;
CREATE TRIGGER trg_projects_updated_at BEFORE UPDATE ON public.projects
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_tasks_set_level ON public.tasks;
CREATE TRIGGER trg_tasks_set_level BEFORE INSERT OR UPDATE OF parent_task_id ON public.tasks
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_task_level();

DROP TRIGGER IF EXISTS trg_tasks_updated_at ON public.tasks;
CREATE TRIGGER trg_tasks_updated_at BEFORE UPDATE ON public.tasks
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_task_comments_updated_at ON public.task_comments;
CREATE TRIGGER trg_task_comments_updated_at BEFORE UPDATE ON public.task_comments
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

-- (b? sung so v?i dump g?c: task_logworks cung c� updated_at)
DROP TRIGGER IF EXISTS trg_task_logworks_updated_at ON public.task_logworks;
CREATE TRIGGER trg_task_logworks_updated_at BEFORE UPDATE ON public.task_logworks
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_news_updated_at ON public.news;
CREATE TRIGGER trg_news_updated_at BEFORE UPDATE ON public.news
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_posts_updated_at ON public.posts;
CREATE TRIGGER trg_posts_updated_at BEFORE UPDATE ON public.posts
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_post_comments_updated_at ON public.post_comments;
CREATE TRIGGER trg_post_comments_updated_at BEFORE UPDATE ON public.post_comments
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_recruit_updated_at ON public.recruitment_posts;
CREATE TRIGGER trg_recruit_updated_at BEFORE UPDATE ON public.recruitment_posts
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_app_updated_at ON public.applications;
CREATE TRIGGER trg_app_updated_at BEFORE UPDATE ON public.applications
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_iv_updated_at ON public.interviews;
CREATE TRIGGER trg_iv_updated_at BEFORE UPDATE ON public.interviews
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_folders_updated_at ON public.folders;
CREATE TRIGGER trg_folders_updated_at BEFORE UPDATE ON public.folders
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_materials_updated_at ON public.materials;
CREATE TRIGGER trg_materials_updated_at BEFORE UPDATE ON public.materials
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

-- TU? CH?N: ph�n h? chat thi?t k? KH�NG trigger (app t? set updated_at).
-- B? comment 3 kh?i du?i n?u mu?n DB t? maintain updated_at cho chat.
/*
DROP TRIGGER IF EXISTS trg_chat_conv_updated_at ON public.chat_conversations;
CREATE TRIGGER trg_chat_conv_updated_at BEFORE UPDATE ON public.chat_conversations
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_msg_updated_at ON public.chat_messages;
CREATE TRIGGER trg_chat_msg_updated_at BEFORE UPDATE ON public.chat_messages
    FOR EACH ROW EXECUTE FUNCTION public.trg_set_updated_at();
*/

-- =====================================================================
-- M?C 11. VIEW CHAT
-- =====================================================================

-- 11.1 Danh s�ch h?i tho?i c?a t?ng user
CREATE OR REPLACE VIEW public.v_chat_user_conversations AS
SELECT
    p.user_id,
    c.id AS conversation_id,
    c.conversation_type,
    CASE WHEN c.conversation_type = 'group' THEN c.name       ELSE peer_u.full_name  END AS display_name,
    CASE WHEN c.conversation_type = 'group' THEN c.avatar_url ELSE peer_u.avatar_url END AS display_avatar_url,
    c.description,
    c.project_id,
    peer_u.id AS peer_user_id,
    p.role AS my_role,
    p.is_muted,
    p.is_pinned,
    c.last_message_id,
    c.last_message_at,
    lm.message_type AS last_message_type,
    lm.sender_id    AS last_message_sender_id,
    lmu.full_name   AS last_message_sender_name,
    CASE
        WHEN lm.id IS NULL THEN NULL
        WHEN lm.deleted_at IS NOT NULL THEN 'Tin nh?n d� du?c thu h?i'
        WHEN lm.message_type = 'image' THEN COALESCE(left(lm.content, 200), '[H�nh ?nh]')
        WHEN lm.message_type = 'file'  THEN COALESCE(left(lm.content, 200), '[T?p d�nh k�m]')
        ELSE left(lm.content, 200)
    END AS last_message_preview,
    COALESCE(unread.cnt, 0) AS unread_count,
    (SELECT count(*) FROM public.chat_participants x
      WHERE x.conversation_id = c.id AND x.left_at IS NULL) AS member_count,
    c.created_at,
    c.updated_at
FROM public.chat_participants p
JOIN public.chat_conversations c
  ON c.id = p.conversation_id AND c.deleted_at IS NULL
LEFT JOIN public.users peer_u
  ON c.conversation_type = 'direct'
 AND peer_u.id = CASE WHEN p.user_id = c.direct_user_low THEN c.direct_user_high ELSE c.direct_user_low END
LEFT JOIN public.chat_messages lm ON lm.id = c.last_message_id
LEFT JOIN public.users lmu        ON lmu.id = lm.sender_id
LEFT JOIN LATERAL (
    SELECT count(*) AS cnt
      FROM public.chat_messages m
     WHERE m.conversation_id = c.id
       AND m.id > COALESCE(p.last_read_message_id, 0)
       AND m.deleted_at IS NULL
       AND m.message_type <> 'system'
       AND m.sender_id IS DISTINCT FROM p.user_id
) unread ON true
WHERE p.left_at IS NULL;

-- 11.2 Tin nh?n d?y d?
CREATE OR REPLACE VIEW public.v_chat_messages AS
SELECT
    m.id,
    m.conversation_id,
    m.sender_id,
    u.full_name  AS sender_name,
    u.avatar_url AS sender_avatar_url,
    m.message_type,
    CASE WHEN m.deleted_at IS NULL THEN m.content END AS content,
    CASE WHEN m.deleted_at IS NULL THEN m.metadata ELSE '{}'::jsonb END AS metadata,
    m.reply_to_message_id,
    CASE WHEN rm.id IS NULL THEN NULL
         ELSE jsonb_build_object(
                'id', rm.id,
                'sender_id', rm.sender_id,
                'sender_name', ru.full_name,
                'message_type', rm.message_type,
                'content', CASE WHEN rm.deleted_at IS NULL THEN left(rm.content, 200) END,
                'is_deleted', (rm.deleted_at IS NOT NULL)
              )
    END AS reply_to,
    CASE WHEN m.deleted_at IS NULL THEN COALESCE(att.items, '[]'::jsonb) ELSE '[]'::jsonb END AS attachments,
    CASE WHEN m.deleted_at IS NULL THEN COALESCE(rea.items, '[]'::jsonb) ELSE '[]'::jsonb END AS reactions,
    COALESCE(men.items, '[]'::jsonb) AS mention_user_ids,
    m.client_message_id,
    (m.deleted_at IS NOT NULL) AS is_deleted,
    (m.edited_at  IS NOT NULL) AS is_edited,
    m.edited_at,
    m.created_at,
    m.deleted_at,
    m.deleted_by
FROM public.chat_messages m
LEFT JOIN public.users u          ON u.id  = m.sender_id
LEFT JOIN public.chat_messages rm ON rm.id = m.reply_to_message_id
LEFT JOIN public.users ru         ON ru.id = rm.sender_id
LEFT JOIN LATERAL (
    SELECT jsonb_agg(jsonb_build_object(
               'id', a.id,
               'file_url', a.file_url,
               'file_name', a.file_name,
               'file_type', a.file_type,
               'file_size', a.file_size
           ) ORDER BY a.id) AS items
      FROM public.chat_message_attachments a
     WHERE a.message_id = m.id
) att ON true
LEFT JOIN LATERAL (
    SELECT jsonb_agg(jsonb_build_object(
               'emoji', g.emoji,
               'count', g.cnt,
               'user_ids', g.user_ids
           ) ORDER BY g.first_id) AS items
      FROM (
            SELECT r.emoji,
                   count(*) AS cnt,
                   jsonb_agg(r.user_id ORDER BY r.id) AS user_ids,
                   min(r.id) AS first_id
              FROM public.chat_message_reactions r
             WHERE r.message_id = m.id
             GROUP BY r.emoji
           ) g
) rea ON true
LEFT JOIN LATERAL (
    SELECT jsonb_agg(mm.user_id ORDER BY mm.id) AS items
      FROM public.chat_message_mentions mm
     WHERE mm.message_id = m.id
) men ON true;

-- 11.3 Th�nh vi�n dang ho?t d?ng
CREATE OR REPLACE VIEW public.v_chat_members AS
SELECT
    p.conversation_id,
    p.user_id,
    u.full_name,
    u.org_email,
    u.avatar_url,
    u.department,
    p.role,
    p.joined_at,
    p.last_read_message_id,
    p.last_read_at,
    p.is_muted,
    p.is_pinned
FROM public.chat_participants p
JOIN public.users u ON u.id = p.user_id
WHERE p.left_at IS NULL;

-- =====================================================================
-- M?C 12. SEED D? LI?U C?U H�NH
-- =====================================================================

INSERT INTO public.notification_settings (notification_type, is_enabled) VALUES
    ('task_assigned',     true),
    ('task_status_change',true),
    ('task_review',       true),
    ('post_published',    true),
    ('post_review',       true),
    ('news_published',    true),
    ('member_added',      true),
    ('member_removed',    true),
    ('interview_scheduled', true),
    ('application_result',  true),
    ('chat_mention',      true),
    ('chat_group_added',  true)
ON CONFLICT (notification_type) DO NOTHING;

INSERT INTO public.system_settings (setting_key, setting_value) VALUES
    ('system_logo_url',         NULL),
    ('session_timeout_minutes', '60'),
    ('lab_intro_content',       NULL),
    ('lab_contact_email',       NULL),
    ('lab_contact_address',     NULL)
ON CONFLICT (setting_key) DO NOTHING;

-- =====================================================================
-- M?C 13. COMMENT (T�I LI?U HO�)
-- =====================================================================

COMMENT ON TABLE  public.users IS 'T�i kho?n ngu?i d�ng h? th?ng. role = vai tr� to�n c?c (kh�c v?i vai tr� trong t?ng project).';
COMMENT ON COLUMN public.users.role   IS 'it_admin=IT Admin, business_admin=BA, manager=Manager, leader=Leader, member=Member, guest=Guest (sinh vi�n ?ng tuy?n, chua l� th�nh vi�n)';
COMMENT ON COLUMN public.users.status IS 'active=�ang ho?t d?ng, inactive=Ng?ng ho?t d?ng, locked=B? kh�a';

COMMENT ON TABLE public.auth_verification_codes IS 'Luu OTP/li�n k?t kh�i ph?c m?t kh?u; ki?m tra expires_at, used_at, attempt_count d? gi?i h?n s? l?n g?i/nh?p l?i.';

COMMENT ON TABLE public.system_settings IS 'C?u h�nh h? th?ng d?ng key-value. C�c key dang d�ng g?m:
     system_logo_url            - Logo h? th?ng
     session_timeout_minutes    - Th?i gian timeout session (ph�t)
     lab_intro_content          - N?i dung gi?i thi?u Lab (Homepage)
     lab_contact_email          - Email li�n h? (Homepage)
     lab_contact_address        - �?a ch? li�n h? (Homepage)';
COMMENT ON TABLE public.notification_settings IS 'B?t/t?t t?ng lo?i th�ng b�o h? th?ng g?i t?i user (IT Admin c?u h�nh).';

COMMENT ON TABLE public.domains IS 'Linh v?c nghi�n c?u (Domain) m� Project tr?c thu?c.';
COMMENT ON TABLE public.project_types IS 'Lo?i project (VD: Nghi�n c?u khoa h?c, D? �n th?c t?, �? �n). Lo?i "inactive" kh�ng du?c ch?n khi t?o project m?i, ch? xo� du?c khi chua c� project n�o d�ng.';

COMMENT ON TABLE  public.projects IS 'D? �n. T�n tr�ng b? ch?n trong ph?m vi c�ng domain (�p d?ng b?ng UNIQUE INDEX uq_projects_name_per_domain).';
COMMENT ON COLUMN public.projects.status IS 'pending=Chua b?t d?u, processing=�ang th?c hi?n, done=Ho�n th�nh, cancel=�� h?y';

COMMENT ON TABLE public.project_members IS 'Th�nh vi�n & vai tr� trong 1 project. owner = Manager ph? tr�ch ch�nh (duy nh?t 1 ngu?i/project, tr�ng owner_id c?a b?ng projects).
     coordinator = �?ng qu?n l� (du?c BA/Owner g�n qua ch?c nang Assign Coordinator), c� to�n quy?n tr? x�a Owner.
     leader = Tru?ng team, member = Th�nh vi�n team (team_leader_member_id tr? t?i Leader ph? tr�ch).
     Quy t?c g? th�nh vi�n: ch? Owner du?c xo� Coordinator; Owner/Coordinator g? du?c Leader, Member; kh�ng ai g? du?c Owner.';

COMMENT ON TABLE  public.tasks IS 'Task (t?i da 3 c?p, qua parent_task_id). Lu?ng tr?ng th�i: pending -> processing -> pending_review -> done/rejected; cancel c� th? x?y ra t? m?i tr?ng th�i tru?c done.';
COMMENT ON COLUMN public.tasks.status IS 'pending=Chua b?t d?u, processing=�ang th?c hi?n, pending_review=Ch? review, done=Ho�n th�nh, rejected=B? t? ch?i, cancel=�� h?y';
COMMENT ON TABLE public.task_assignees IS 'Danh s�ch ngu?i du?c giao th?c hi?n 1 task (quan h? N-N gi?a task v� user).';
COMMENT ON TABLE public.task_comments IS 'B�nh lu?n trong task. Ch? t�c gi? (user_id) du?c s?a/x�a b�nh lu?n c?a ch�nh m�nh (�p d?ng ? t?ng ?ng d?ng).';
COMMENT ON TABLE public.task_logs IS 'Audit log thao t�c tr�n task: t?o, s?a tru?ng, d?i ngu?i th?c hi?n, d?i tr?ng th�i, n?p, duy?t, h?y.';
COMMENT ON TABLE  public.task_logworks IS 'User-recorded actual work on a task; task_logs is an audit log, not a timesheet.';
COMMENT ON COLUMN public.task_logworks.duration_minutes IS 'Actual minutes worked (1..1440); if start/end supplied, must match whole minutes.';

COMMENT ON COLUMN public.news.status IS 'draft=Nh�p, scheduled=�� l�n l?ch, published=�� dang, hidden=?n';
COMMENT ON TABLE public.posts IS 'Post do Manager dang th� t? d?ng published (kh�ng c?n duy?t); post do Member g?i ? tr?ng th�i pending_review ch? Manager duy?t.';
COMMENT ON TABLE public.post_attachments IS 'File d? li?u/t�i li?u d�nh k�m theo Post (1 post c� th? c� nhi?u file).';
COMMENT ON TABLE public.post_comments IS 'B�nh lu?n du?i Post. T�c gi? t? s?a/xo� b�nh lu?n c?a m�nh; Manager c� th? xo� b�nh lu?n vi ph?m c?a ngu?i kh�c k�m l� do (delete_reason).';

COMMENT ON TABLE public.recruitment_posts IS 'B�i dang tuy?n th�nh vi�n m?i cho Lab (kh�c Apply Project - vi?c Member xin v�o 1 project d� c� s?n).';
COMMENT ON TABLE public.recruitment_form_fields IS 'Danh s�ch tru?ng th�ng tin m� ?ng vi�n ph?i di?n, do Manager c?u h�nh khi t?o b�i tuy?n.';
COMMENT ON TABLE  public.applications IS '�on ?ng tuy?n. Khi status=passed v� du?c x�c nh?n, h? th?ng g?i request d? IT Admin c?p t�i kho?n (created_user_id).';
COMMENT ON COLUMN public.applications.status IS 'pending=Ch? duy?t, interview=H?n ph?ng v?n, passed=�?t, failed=Kh�ng d?t, withdrawn=�� r�t';
COMMENT ON TABLE public.interviews IS 'L?ch ph?ng v?n g?n v?i 1 don ?ng tuy?n. 1 application c� th? c� nhi?u interviews (n?u ph?ng v?n l?i nhi?u v�ng).';

COMMENT ON TABLE public.folders IS 'C�y thu m?c t�i li?u. Kh�ng xo� du?c n?u c�n material ho?c c�n sub-folder b�n trong (ki?m tra ? t?ng ?ng d?ng tru?c khi soft-delete).';
COMMENT ON TABLE public.materials IS 'T�i li?u/file trong 1 folder. Member/Leader ch? du?c xo� material do ch�nh m�nh upload (ki?m tra uploaded_by ? t?ng ?ng d?ng); Manager/Admin xo� du?c to�n b?.';
COMMENT ON TABLE public.notifications IS 'Th�ng b�o g?i t?i t?ng user, sinh ra b?i c�c h�nh d?ng: giao task, duy?t/t? ch?i, dang news/post, th�m/g? th�nh vi�n, l?ch ph?ng v?n, v.v.';
COMMENT ON TABLE public.report_exports IS 'L?ch s? c�c l?n xu?t b�o c�o (Excel/PDF/Docs) theo h?c k?/nam, g?m c? b�o c�o AI (Export AI v?i Hard Prompt).';

COMMENT ON TABLE  public.chat_conversations IS 'Cu?c tr� chuy?n. direct = chat 1-1 (duy nh?t theo c?p direct_user_low < direct_user_high); group = chat nh�m, c� th? g?n project_id (m?i project t?i da 1 nh�m chat dang ho?t d?ng).';
COMMENT ON COLUMN public.chat_conversations.direct_user_low  IS 'Chat 1-1: id user nh? hon trong c?p. NULL v?i group.';
COMMENT ON COLUMN public.chat_conversations.direct_user_high IS 'Chat 1-1: id user l?n hon trong c?p. NULL v?i group.';
COMMENT ON COLUMN public.chat_conversations.last_message_id  IS 'Tin nh?n m?i nh?t; ?ng d?ng c?p nh?t khi g?i tin, d�ng d? s?p x?p danh s�ch h?i tho?i.';
COMMENT ON TABLE  public.chat_participants IS 'Th�nh vi�n h?i tho?i. left_at IS NOT NULL = d� r?i/b? x�a (gi? l?ch s?, c� th? th�m l?i). M?i cu?c tr� chuy?n c� t?i da 1 owner dang ho?t d?ng. last_read_message_id d�ng t�nh tin chua d?c.';
COMMENT ON TABLE  public.chat_messages IS 'Tin nh?n. system = th�ng b�o h? th?ng (t?o nh�m, th�m/x�a th�nh vi�n...). Thu h?i = x�a m?m (deleted_at); view v_chat_messages che n?i dung. client_message_id do client sinh (UUID) d? ch?ng g?i tr�ng khi retry.';
COMMENT ON COLUMN public.chat_messages.metadata IS 'D? li?u ph?, v� d? tin system: {"event":"member_added","actor_id":1,"target_user_ids":[2,3]}.';
COMMENT ON TABLE public.chat_message_attachments IS 'T?p/?nh d�nh k�m c?a tin nh?n (gi?i h?n s? t?p do ?ng d?ng ki?m so�t).';
COMMENT ON TABLE public.chat_message_reactions IS 'C?m x�c (emoji) c?a th�nh vi�n tr�n tin nh?n; m?i user ch? th? 1 l?n m?i emoji.';
COMMENT ON TABLE public.chat_message_mentions IS 'Danh s�ch user du?c @nh?c d?n trong tin nh?n.';
COMMENT ON VIEW  public.v_chat_user_conversations IS 'Danh s�ch h?i tho?i theo user: SELECT * FROM v_chat_user_conversations WHERE user_id = ? ORDER BY is_pinned DESC, last_message_at DESC NULLS LAST.';
COMMENT ON VIEW  public.v_chat_messages IS 'Tin nh?n d� ho�n thi?n (reply, attachments, reactions, mentions). Tin thu h?i b? che n?i dung.';
COMMENT ON VIEW  public.v_chat_members IS 'Th�nh vi�n dang ho?t d?ng c?a t?ng cu?c tr� chuy?n.';



-- =====================================================================
-- TRIGGERS CHO CẬP NHẬT updated_at TỰ ĐỘNG
-- =====================================================================
DROP TRIGGER IF EXISTS trg_auth_verification_codes_updated_at ON public.auth_verification_codes;
CREATE TRIGGER trg_auth_verification_codes_updated_at
    BEFORE UPDATE ON public.auth_verification_codes
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_system_settings_updated_at ON public.system_settings;
CREATE TRIGGER trg_system_settings_updated_at
    BEFORE UPDATE ON public.system_settings
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_notification_settings_updated_at ON public.notification_settings;
CREATE TRIGGER trg_notification_settings_updated_at
    BEFORE UPDATE ON public.notification_settings
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_project_types_updated_at ON public.project_types;
CREATE TRIGGER trg_project_types_updated_at
    BEFORE UPDATE ON public.project_types
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_project_members_updated_at ON public.project_members;
CREATE TRIGGER trg_project_members_updated_at
    BEFORE UPDATE ON public.project_members
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_task_assignees_updated_at ON public.task_assignees;
CREATE TRIGGER trg_task_assignees_updated_at
    BEFORE UPDATE ON public.task_assignees
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_task_logs_updated_at ON public.task_logs;
CREATE TRIGGER trg_task_logs_updated_at
    BEFORE UPDATE ON public.task_logs
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_post_attachments_updated_at ON public.post_attachments;
CREATE TRIGGER trg_post_attachments_updated_at
    BEFORE UPDATE ON public.post_attachments
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_recruitment_posts_updated_at ON public.recruitment_posts;
CREATE TRIGGER trg_recruitment_posts_updated_at
    BEFORE UPDATE ON public.recruitment_posts
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_recruitment_form_fields_updated_at ON public.recruitment_form_fields;
CREATE TRIGGER trg_recruitment_form_fields_updated_at
    BEFORE UPDATE ON public.recruitment_form_fields
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_applications_updated_at ON public.applications;
CREATE TRIGGER trg_applications_updated_at
    BEFORE UPDATE ON public.applications
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_interviews_updated_at ON public.interviews;
CREATE TRIGGER trg_interviews_updated_at
    BEFORE UPDATE ON public.interviews
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_notifications_updated_at ON public.notifications;
CREATE TRIGGER trg_notifications_updated_at
    BEFORE UPDATE ON public.notifications
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_report_exports_updated_at ON public.report_exports;
CREATE TRIGGER trg_report_exports_updated_at
    BEFORE UPDATE ON public.report_exports
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_conversations_updated_at ON public.chat_conversations;
CREATE TRIGGER trg_chat_conversations_updated_at
    BEFORE UPDATE ON public.chat_conversations
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_messages_updated_at ON public.chat_messages;
CREATE TRIGGER trg_chat_messages_updated_at
    BEFORE UPDATE ON public.chat_messages
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_participants_updated_at ON public.chat_participants;
CREATE TRIGGER trg_chat_participants_updated_at
    BEFORE UPDATE ON public.chat_participants
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_message_attachments_updated_at ON public.chat_message_attachments;
CREATE TRIGGER trg_chat_message_attachments_updated_at
    BEFORE UPDATE ON public.chat_message_attachments
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_message_reactions_updated_at ON public.chat_message_reactions;
CREATE TRIGGER trg_chat_message_reactions_updated_at
    BEFORE UPDATE ON public.chat_message_reactions
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

DROP TRIGGER IF EXISTS trg_chat_message_mentions_updated_at ON public.chat_message_mentions;
CREATE TRIGGER trg_chat_message_mentions_updated_at
    BEFORE UPDATE ON public.chat_message_mentions
    FOR EACH ROW
    EXECUTE FUNCTION public.trg_set_updated_at();

COMMIT;

-- =====================================================================
-- M?C 14. KI?M TRA SAU KHI CH?Y
-- =====================================================================

-- 14.1 �?m object
SELECT
    (SELECT count(*) FROM information_schema.tables  WHERE table_schema='public' AND table_type='BASE TABLE') AS tables_count,
    (SELECT count(*) FROM information_schema.views   WHERE table_schema='public')                             AS views_count,
    (SELECT count(*) FROM pg_trigger t JOIN pg_class c ON c.oid=t.tgrelid
      JOIN pg_namespace n ON n.oid=c.relnamespace
     WHERE n.nspname='public' AND NOT t.tgisinternal)                                                        AS triggers_count,
    (SELECT count(*) FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace
     WHERE n.nspname='public' AND p.proname LIKE 'trg\_%')                                                   AS functions_count;

-- 14.2 Li?t k� to�n b? b?ng + view
SELECT table_name AS object_name, table_type AS object_type
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_type, table_name;