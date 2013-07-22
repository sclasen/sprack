require "bundler/gem_tasks"

namespace :sprack do
  desc "build the gem"
  task :jar do
   puts "OHAI"
   exec 'sbt clean compile assembly'
   puts "DONE"
  end
  desc "build the gem"
  task :gem do
   puts "OHAI"
   exec 'gem build sprack.gemspec'
   puts "DONE"
  end
end
