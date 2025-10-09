import { createClient } from '@supabase/supabase-js'

const supabaseUrl = 'https://kqyfwrtdqfhgogsxngdr.supabase.co'
const supabaseAnonKey = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImtxeWZ3cnRkcWZoZ29nc3huZ2RyIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTkzOTg5MzAsImV4cCI6MjA3NDk3NDkzMH0.PlMsdjYTXKedCgklb3tQGl_nBa4MVXIKIxQlStHvc9k'

export const supabase = createClient(supabaseUrl, supabaseAnonKey)