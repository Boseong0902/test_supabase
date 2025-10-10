import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://breuvgvnhsjbjxrauknv.supabase.co'
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImJyZXV2Z3ZuaHNqYmp4cmF1a252Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjAwNzU0OTEsImV4cCI6MjA3NTY1MTQ5MX0.G3SqwDC6Vmnsj-YQIUYJ96sZfe1AHzt74mns2rbtRcY'

export const supabase = createClient(supabaseUrl, supabaseAnonKey)