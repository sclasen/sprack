require "bundler/gem_tasks"

namespace :sprack do
  desc "build the gem"
  task :gem do
   puts "OHAI"
   exec 'sbt clean compile assembly'
   exec 'gem build sprack.gemspec'
  end
end
